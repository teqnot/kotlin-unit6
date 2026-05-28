package com.example.blescanner.data.model

import android.bluetooth.BluetoothDevice
import android.os.ParcelUuid
import java.util.UUID

data class BleDevice(
    val address: String,
    val name: String?,
    val rssi: Int,
    val device: BluetoothDevice? = null,
    val serviceData: ByteArray? = null,
    val services: List<ParcelUuid> = emptyList()
) {
    val displayName: String
        get() = name ?: "Unknown Device"

    val formattedRssi: String
        get() = "$rssi dBm"

    fun hasHeartRateService(): Boolean {
        val heartRateServiceUuid = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")
        return services.any { it.uuid == heartRateServiceUuid }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as BleDevice
        return address == other.address
    }

    override fun hashCode(): Int {
        return address.hashCode()
    }
}