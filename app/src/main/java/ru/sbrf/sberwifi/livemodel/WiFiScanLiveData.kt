package ru.sbrf.sberwifi.livemodel

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.util.Log
import androidx.lifecycle.LiveData

/**
 * Класс-источник данных результатов сканирования WiFi-данных по event-системы
 */
public class WiFiScanLiveData(private val context: Context) : LiveData<WiFiScan>() {
    private var broadcastReceiver: BroadcastReceiver? = null

    private fun prepareReceiver(context: Context) {
        val wifi = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

        //подписываемся на широковещательные события результатов сканирования WiFI-сети
        val wifiScanReceiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {
                val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
                handlerResultScan(success, wifi)
            }
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        context.registerReceiver(wifiScanReceiver, intentFilter)
    }

    /**
     * Выполняем первое принудительное сканирование WiFi-сетей
     */
    private fun primaryScan() {
        val wifi = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val success = wifi.startScan()
        handlerResultScan(success, wifi)
    }

    fun handlerResultScan(success: Boolean, wifi: WifiManager) {
        if (success) {
            Log.i("Scan", "Success")
            scanSuccess(wifi)
        } else {
            Log.e("Scan", "Error")
            scanFailure(wifi)
        }
    }

    override fun onActive() {
        super.onActive()
        primaryScan()
        prepareReceiver(context)
    }

    override fun onInactive() {
        super.onInactive()
        context.unregisterReceiver(broadcastReceiver)
        broadcastReceiver = null
    }

    private fun scanSuccess(wifiManager: WifiManager) {

        val results = wifiManager.scanResults
        val wifiInfo = wifiManager.connectionInfo
        val wiFiScan = WiFiScan(results, wifiInfo)

        value = wiFiScan
    }

    private fun scanFailure(wifiManager: WifiManager) {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        val results = wifiManager.scanResults
    }
}