package ru.sbrf.sberwifi.livemodel.scheduler

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class WiFiServiceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("Test", "COOOL!!")
        Toast.makeText(context, "COL!", Toast.LENGTH_LONG).show()

    }
}