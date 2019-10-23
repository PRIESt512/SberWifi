package ru.sbrf.sberwifi.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import org.apache.commons.lang3.StringUtils
import ru.sbrf.sberwifi.MainContext
import ru.sbrf.sberwifi.R
import ru.sbrf.sberwifi.http.report.PostOfReportUseCase
import ru.sbrf.sberwifi.wifi.model.WiFiData
import kotlin.coroutines.CoroutineContext

// the fragment initialization parameters
private const val VIEW_MODEL_PARAM = "listData"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ReportFragment.OnReportInteractionListener] interface
 * to handle interaction events.
 * Use the [ReportFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReportFragment : Fragment(), CoroutineScope {

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private var callback: OnReportInteractionListener? = null

    private var list4Report: WiFiData = MainContext.INSTANCE.wiFiData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val viewFragment = inflater.inflate(R.layout.fragment_report, container, false)

        val sendButton = viewFragment.findViewById<Button>(R.id.sendReport)

        sendButton.setOnClickListener {
            launch {
                val report = sendReport()
                if (report.status == 200) {
                    Toast.makeText(this@ReportFragment.context, "Отчет отправлен успешно", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ReportFragment.context, "Отчет не отправлен; Статус ${report.status}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return viewFragment
    }

    private suspend fun sendReport(): PostOfReportUseCase.Result = withContext(Dispatchers.IO) {
        list4Report.getWiFiDetails().forEach {
            it.wiFiAdditional.vendorName = MainContext.INSTANCE.vendorService.findVendorName(it.bssid)
        }

        val reportCase = PostOfReportUseCase()

        try {
            val result = reportCase.doWork(list4Report)

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


    fun setOnReportListener(callback: OnReportInteractionListener) {
        this.callback = callback
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnReportInteractionListener) {
            callback = context
        } else {
            throw RuntimeException("$context must implement OnReportInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    interface OnReportInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        @JvmStatic
        fun newInstance() = ReportFragment()
    }
}
