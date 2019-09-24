package ru.sbrf.sberwifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    var list: ParallaxScrollListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        list = findViewById(R.id.layout_listview)
        list?.setZoomRatio(ParallaxScrollListView.ZOOM_X2)

        val header = LayoutInflater.from(this).inflate(R.layout.listview_header, null)
        val mImageView = header.findViewById(R.id.layout_header_image) as ImageView

        list?.setParallaxImageView(mImageView)
        list?.addHeaderView(header)

//        val adapter = ArrayAdapter(this,
//                android.R.layout.simple_expandable_list_item_1,
//                arrayOf("First Item", "Second Item", "Third Item", "Fifth Item", "Sixth Item", "Seventh Item", "Eighth Item", "Ninth Item", "Tenth Item", ".....")
//        )
//
//        list?.adapter = adapter

        startScan()
    }

    private fun startScan() {
        val wifi = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val wifiScanReceiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {
                val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
                if (success) {
                    Log.i("Scan", "Success")
                    scanSuccess(wifi)
                } else {
                    Log.e("Scan", "Error")
                    scanFailure(wifi)
                }
            }
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        applicationContext.registerReceiver(wifiScanReceiver, intentFilter)

        val successfull = wifi.startScan()
        if (!successfull) return

        scanSuccess(wifi)
    }

    private fun scanSuccess(wifiManager: WifiManager) {
        val results = wifiManager.scanResults

        val listWiFi = ArrayList<ResultWiFi>()

        for (item in results) {
            listWiFi.add(ResultWiFi(item.SSID, item.level))
        }

        val adapter = ArrayAdapter(this,
                android.R.layout.simple_expandable_list_item_1,
                listWiFi)

        list?.adapter = adapter
    }

    private fun scanFailure(wifiManager: WifiManager) {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        val results = wifiManager.scanResults
    }

    private class ResultWiFi constructor(val SSID: String, val level: Int){
        override fun toString(): String {
            return String.format("SSID: %s, Level: %sdBm", SSID, level)
        }
    }
}