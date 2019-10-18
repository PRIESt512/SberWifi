package ru.sbrf.sberwifi.livemodel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sbrf.sberwifi.wifi.model.WiFiData

/**
 * Перехватчик событий обновления данных по результатам сканирования WiFi-сети
 */
public class DetectorViewModel(application: Application) : AndroidViewModel(application) {

    private val wifiScan: WiFiScanLiveData = WiFiScanLiveData(application.applicationContext)
    private val resultScanObserver: Observer<WiFiData>
    private val scanMediatorLiveData: MediatorLiveData<WiFiData> = MediatorLiveData()

    val resultScanLiveData: MutableLiveData<WiFiData> by lazy {
        MutableLiveData<WiFiData>()
    }

    private val transformer: Transformer = Transformer()

    init {
        resultScanObserver = Observer {
            resultScanLiveData.value = it
        }

        scanMediatorLiveData.addSource(wifiScan) {
            viewModelScope.launch {
                val result = transform(it)
                scanMediatorLiveData.value = result
            }
        }
        scanMediatorLiveData.observeForever(resultScanObserver)
    }

    private suspend fun transform(scan: WiFiScan) = withContext(Dispatchers.Default) {
        return@withContext transformer.transformToWiFiData(scan.scanResult, scan.wifiInfo)
    }
}