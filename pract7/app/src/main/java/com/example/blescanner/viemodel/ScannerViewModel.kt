package com.example.blescanner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blescanner.BleDevice
import com.example.blescanner.BleManager
import com.example.blescanner.ConnectionState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ScannerViewModel(private val bleManager: BleManager) : ViewModel() {

    val devices: StateFlow<List<BleDevice>> = bleManager.scanResults
    val connectionState: StateFlow<ConnectionState> = bleManager.connectionState
    val heartRate: StateFlow<Int?> = bleManager.heartRate

    val isScanning: StateFlow<Boolean> = bleManager.scanResults
        .map { false }
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    fun startScan() {
        bleManager.startScan()
    }

    fun stopScan() {
        bleManager.stopScan()
    }

    fun connect(device: BleDevice) {
        viewModelScope.launch {
            bleManager.connect(device)
            bleManager.startMockHeartRate()
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            bleManager.disconnect()
            bleManager.stopMockHeartRate()
        }
    }

    override fun onCleared() {
        super.onCleared()
        bleManager.close()
    }
}