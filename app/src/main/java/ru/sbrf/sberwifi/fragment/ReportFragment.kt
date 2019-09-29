package ru.sbrf.sberwifi.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import ru.sbrf.sberwifi.R
import ru.sbrf.sberwifi.ResultWiFi
import ru.sbrf.sberwifi.livemodel.DetectorViewModel
import ru.sbrf.sberwifi.wifi.model.WiFiData

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
class ReportFragment : Fragment() {
    private var callback: OnReportInteractionListener? = null

    private lateinit var list4Report: WiFiData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            //list4Report = it.getParcelableArrayList(VIEW_MODEL_PARAM)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val viewFragment = inflater.inflate(R.layout.fragment_report, container, false)

        val sendButton = viewFragment.findViewById<Button>(R.id.sednReport)
        val gson = Gson()
        val client = OkHttpClient()

        val viewModel = ViewModelProviders.of(this).get(DetectorViewModel::class.java)

        viewModel.getResultScanLiveData().observe(this, Observer {
            list4Report = it
        })

        sendButton.setOnClickListener {
            when {
                list4Report != null -> {
                    val report = gson.toJson(list4Report)
                    val body = report.toRequestBody("application/json; charset=utf-8".toMediaType())
                    val request = Request.Builder()
                            .url("http")
                            .post(body)
                            .build()

                    client.newCall(request).execute().use { response -> response.body!!.string() }
                }
            }
        }
        return viewFragment
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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param data list data scan WiFi
         * @return A new instance of fragment ReportFragment.
         */
        @JvmStatic
        fun newInstance(data: ArrayList<ResultWiFi>) =
                ReportFragment().apply {
                    arguments = Bundle().apply {
                        putParcelableArrayList(VIEW_MODEL_PARAM, data)
                    }
                }

        @JvmStatic
        fun newInstance() = ReportFragment()
    }
}
