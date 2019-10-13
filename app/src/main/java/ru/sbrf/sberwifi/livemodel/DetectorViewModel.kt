package ru.sbrf.sberwifi.livemodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import ru.sbrf.sberwifi.wifi.model.WiFiData

/**
 * Перехватчик событий обновления данных по результатам сканирования WiFi-сети
 */
public class DetectorViewModel(application: Application) : AndroidViewModel(application) {

    private val wifiScan: WiFiScanLiveData = WiFiScanLiveData(application.applicationContext)
    private val resultScanLiveData: MutableLiveData<WiFiData> = MutableLiveData()
    private val resultScanObserver: Observer<WiFiData>
    private val scanMediatorLiveData: MediatorLiveData<WiFiData> = MediatorLiveData()

    private val transformer: Transformer = Transformer()

    init {
        resultScanObserver = Observer {
            resultScanLiveData.value = it
        }

        scanMediatorLiveData.addSource(wifiScan) {
            val wiFiData = transformer.transformToWiFiData(it.scanResult, it.wifiInfo)
            scanMediatorLiveData.value = wiFiData
        }
        scanMediatorLiveData.observeForever(resultScanObserver)
    }

    public fun getResultScanLiveData(): MutableLiveData<WiFiData> {
        return resultScanLiveData
    }
}