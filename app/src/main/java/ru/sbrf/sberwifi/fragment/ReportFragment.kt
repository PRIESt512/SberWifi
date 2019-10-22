package ru.sbrf.sberwifi.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import ru.sbrf.sberwifi.MainContext
import ru.sbrf.sberwifi.R
import ru.sbrf.sberwifi.http.PostOfReportUseCase
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

        sendButton.setOnClickListener { sendReport() }
        return viewFragment
    }

    private fun sendReport() {
        list4Report.getWiFiDetails().forEach {
            it.wiFiAdditional.vendorName = MainContext.INSTANCE.vendorService.findVendorName(it.bssid)
        }

        val useCase = PostOfReportUseCase()
        val json = Json(JsonConfiguration.Stable)
        val report = json.stringify(WiFiData.serializer(), list4Report)

        launch {
            withContext(Dispatchers.IO) {
                val result = useCase.doWork(report)
                result.posts
                Log.d("Report", result.posts!!)
            }
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
