package ru.sbrf.sberwifi.livemodel.scheduler

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.*
import org.apache.commons.lang3.StringUtils
import ru.sbrf.sberwifi.http.report.PostOfReportUseCase
import ru.sbrf.sberwifi.livemodel.Transformer
import ru.sbrf.sberwifi.wifi.model.WiFiData
import kotlin.coroutines.CoroutineContext


class AlarmWiFiScanService : Service(), CoroutineScope {

    private val transformer: Transformer = Transformer()

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main


    override fun onCreate() {
        super.onCreate()
        job = Job()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Toast.makeText(applicationContext, "Start Scan", Toast.LENGTH_LONG).show()

        val wifi = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        launch {
            reportScan(wifi)
        }
        return START_NOT_STICKY
    }

    private suspend fun reportScan(wifi: WifiManager) = withContext(Dispatchers.Default) {
        val success = wifi.startScan()
        if (success) {
            Log.i("Scan", "Success")
            sendReport(scanSuccess(wifi))
        } else {
            Log.e("Scan", "Error")
        }
    }

    private suspend fun sendReport(wifi: WiFiData): PostOfReportUseCase.Result = withContext(Dispatchers.IO) {

        val reportCase = PostOfReportUseCase()
        try {
            val result = reportCase.doWork(wifi)

            if (result.status == 200)
                return@withContext result
            else {
                Log.d("Report", "request status ${result.status}")
                return@withContext result
            }

        } catch (ex: Exception) {
            Log.e("Report", ex.toString())
            return@withContext PostOfReportUseCase.Result(0, StringUtils.EMPTY)
        }
    }

    private fun scanSuccess(wifiManager: WifiManager): WiFiData {

        val results = wifiManager.scanResults
        val wifiInfo = wifiManager.connectionInfo
        return transformer.transformToWiFiData(results, wifiInfo)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }


    /*override fun onStartJob(params: JobParameters?): Boolean {
        Log.i("Test", " onStartJob COOOL!!")

        Toast.makeText(applicationContext, "onStartJob!", Toast.LENGTH_LONG).show()
        doTask(params)
        return true
    }

    private fun doTask(params: JobParameters?) {
        try {
            Thread.sleep(2000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        jobFinished(params, false)
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }*/

    /* override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
         val message = "AlarmWiFiScanService onStartCommand() method."
         Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()

         return super.onStartCommand(intent, flags, startId)
     }
 */
    companion object {
        public const val SCAN_RESULTS = "ru.sbrf.sberwifi.SCAN_RESULTS"
        public const val ACTION_ALARM = "ru.sbrf.sberwifi.ACTION_ALARM"
    }
}