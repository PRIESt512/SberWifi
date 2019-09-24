package ru.sbrf.sberwifi

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

public class DetectorViewModel(application: Application) : AndroidViewModel(application) {

    private val wifiScan: WiFiScanLiveData = WiFiScanLiveData(application.applicationContext)
    private val resultScanLiveData: MutableLiveData<List<ResultWiFi>> = MutableLiveData()
    private val resultScanObserver: Observer<List<ResultWiFi>> = Observer { resultScanLiveData.value = it }
    private val scanMediatorLiveData: MediatorLiveData<List<ResultWiFi>> = MediatorLiveData()

    init {
        scanMediatorLiveData.addSource(wifiScan) { scanMediatorLiveData.value = it }
        scanMediatorLiveData.observeForever(resultScanObserver)
    }

    public fun getResultScanLiveData(): MutableLiveData<List<ResultWiFi>> {
        return resultScanLiveData
    }
}