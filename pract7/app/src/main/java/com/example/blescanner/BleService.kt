package com.example.blescanner

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class BleService : Service() {

    private val binder = LocalBinder()
    lateinit var bleManager: BleManager
        private set

    inner class LocalBinder : Binder() {
        fun getService(): BleService = this@BleService
    }

    override fun onCreate() {
        super.onCreate()
        bleManager = BleManager(applicationContext)
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        bleManager.close()
    }
}