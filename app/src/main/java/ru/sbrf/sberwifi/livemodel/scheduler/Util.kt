package ru.sbrf.sberwifi.livemodel.scheduler

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.util.Log
import ru.sbrf.sberwifi.util.BuildUtils

class Util {

    companion object {

        public fun scheduleJob(context: Context) {
            val serviceComponent = ComponentName(context, AlarmWiFiScanService::class.java)
            val jobInfo: JobInfo

            if (BuildUtils.isMinVersionN) {
                jobInfo = JobInfo.Builder(1, serviceComponent)
                        .setPeriodic(3 * 1000, 1000)
                        .setPersisted(true).build()
            } else {
                jobInfo = JobInfo.Builder(1, serviceComponent)
                        .setPeriodic(3 * 1000)
                        .setPersisted(true).build()
            }

            val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            if (jobScheduler.schedule(jobInfo) == JobScheduler.RESULT_SUCCESS) {
                Log.i("Job", "Job scheduled successfully")
            }
        }
    }
}