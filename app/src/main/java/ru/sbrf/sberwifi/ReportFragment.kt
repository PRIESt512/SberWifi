package ru.sbrf.sberwifi

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody


// the fragment initialization parameters
private const val VIEW_MODEL_PARAM = "viewModel"

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

    private var list4Report: List<ResultWiFi>? = null

    private lateinit var viewFragment: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            list4Report = it.getParcelableArrayList(VIEW_MODEL_PARAM)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        viewFragment = inflater.inflate(R.layout.fragment_report, container, false)

        val sendButton = viewFragment.findViewById<Button>(R.id.sednReport)
        val gson = Gson()
        val client = OkHttpClient()

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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnReportInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param data
         * @return A new instance of fragment ReportFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(data: ArrayList<ResultWiFi>) =
                ReportFragment().apply {
                    arguments = Bundle().apply {
                        putParcelableArrayList(VIEW_MODEL_PARAM, data)
                    }
                }
    }
}
