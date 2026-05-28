package com.example.blescanner

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.os.Build
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class BleManager(private val context: Context) {

    private val bluetoothManager: BluetoothManager by lazy {
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        bluetoothManager.adapter
    }

    private var bluetoothGatt: BluetoothGatt? = null
    private var heartRateCharacteristic: BluetoothGattCharacteristic? = null

    private val HEART_RATE_SERVICE_UUID = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")
    private val HEART_RATE_MEASUREMENT_UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")
    private val CLIENT_CHARACTERISTIC_CONFIG_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _heartRate = MutableStateFlow<Int?>(null)
    val heartRate: StateFlow<Int?> = _heartRate.asStateFlow()

    private val _scanResults = MutableStateFlow<List<BleDevice>>(emptyList())
    val scanResults: StateFlow<List<BleDevice>> = _scanResults.asStateFlow()

    private var isScanning = false
    private var scanCallback: ScanCallback? = null

    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.d("BLE", "Connected to GATT server")
                    _connectionState.value = ConnectionState.Connecting
                    gatt.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.d("BLE", "Disconnected from GATT server")
                    _connectionState.value = ConnectionState.Disconnected
                    _heartRate.value = null
                    bluetoothGatt?.close()
                    bluetoothGatt = null
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("BLE", "Services discovered")
                discoverHeartRateService(gatt)
            } else {
                Log.e("BLE", "Service discovery failed: $status")
                _connectionState.value = ConnectionState.Disconnected
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, value: ByteArray) {
            if (characteristic.uuid == HEART_RATE_MEASUREMENT_UUID) {
                parseHeartRate(value)
            }
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, value: ByteArray, status: Int) {
            if (characteristic.uuid == HEART_RATE_MEASUREMENT_UUID && status == BluetoothGatt.GATT_SUCCESS) {
                parseHeartRate(value)
            }
        }

        override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            if (descriptor.uuid == CLIENT_CHARACTERISTIC_CONFIG_UUID && status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("BLE", "Notifications enabled")
                _connectionState.value = ConnectionState.Connected
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startScan() {
        if (isScanning) return

        val adapter = bluetoothAdapter ?: run {
            Log.e("BLE", "BluetoothAdapter not initialized")
            return
        }

        if (!adapter.isEnabled) {
            Log.e("BLE", "Bluetooth not enabled")
            return
        }

        _scanResults.value = emptyList()
        isScanning = true

        scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                val device = BleDevice(
                    address = result.device.address,
                    name = result.device.name,
                    rssi = result.rssi,
                    device = result.device,
                    services = result.scanRecord?.serviceUuids ?: emptyList()
                )

                val currentList = _scanResults.value.toMutableList()
                if (!currentList.contains(device)) {
                    currentList.add(device)
                    _scanResults.value = currentList.sortedByDescending { it.rssi }
                }
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>) {
                results.forEach { result ->
                    onScanResult(ScanSettings.CALLBACK_TYPE_ALL_MATCHES, result)
                }
            }

            override fun onScanFailed(errorCode: Int) {
                Log.e("BLE", "Scan failed with error: $errorCode")
                isScanning = false
            }
        }

        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        val scanFilter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid(HEART_RATE_SERVICE_UUID))
            .build()

        adapter.bluetoothLeScanner.startScan(listOf(scanFilter), scanSettings, scanCallback!!)

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            stopScan()
        }, 10000)
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        if (!isScanning) return

        val adapter = bluetoothAdapter ?: return
        scanCallback?.let {
            adapter.bluetoothLeScanner.stopScan(it)
        }
        isScanning = false
        Log.d("BLE", "Scan stopped, found ${_scanResults.value.size} devices")
    }

    @SuppressLint("MissingPermission")
    fun connect(device: BleDevice) {
        val bluetoothDevice = device.device ?: run {
            Log.e("BLE", "BluetoothDevice is null")
            return
        }

        _connectionState.value = ConnectionState.Connecting

        bluetoothGatt = bluetoothDevice.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
        Log.d("BLE", "Attempting to connect to ${device.address}")
    }

    fun disconnect() {
        bluetoothGatt?.disconnect()
        _connectionState.value = ConnectionState.Disconnected
    }

    private fun discoverHeartRateService(gatt: BluetoothGatt) {
        val heartRateService = gatt.getService(HEART_RATE_SERVICE_UUID)

        if (heartRateService != null) {
            Log.d("BLE", "Heart Rate Service found")
            heartRateCharacteristic = heartRateService.getCharacteristic(HEART_RATE_MEASUREMENT_UUID)

            if (heartRateCharacteristic != null) {
                enableNotifications(gatt)
            } else {
                Log.e("BLE", "Heart Rate Measurement characteristic not found")
                _connectionState.value = ConnectionState.Disconnected
            }
        } else {
            Log.e("BLE", "Heart Rate Service not found")
            _connectionState.value = ConnectionState.Disconnected
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableNotifications(gatt: BluetoothGatt) {
        val characteristic = heartRateCharacteristic ?: return

        gatt.setCharacteristicNotification(characteristic, true)

        val descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID)
        descriptor?.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        gatt.writeDescriptor(descriptor)
    }

    private fun parseHeartRate(value: ByteArray) {
        if (value.isEmpty()) return

        val flags = value[0].toInt() and 0x01
        val heartRate = if (flags == 0) {
            value[1].toInt() and 0xFF
        } else {
            ((value[1].toInt() and 0xFF) or ((value[2].toInt() and 0xFF) shl 8))
        }

        Log.d("BLE", "Heart Rate: $heartRate bpm")
        _heartRate.value = heartRate
    }

    fun close() {
        bluetoothGatt?.close()
        bluetoothGatt = null
    }

    fun startMockHeartRate() {
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(object : Runnable {
            override fun run() {
                if (_connectionState.value == ConnectionState.Connected) {
                    val mockRate = (60..100).random()
                    _heartRate.value = mockRate
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(this, 2000)
                }
            }
        }, 1000)
    }

    fun stopMockHeartRate() {
        _heartRate.value = null
    }
}

sealed class ConnectionState {
    object Disconnected : ConnectionState()
    object Connecting : ConnectionState()
    object Connected : ConnectionState()
}