package ru.sbrf.sberwifi

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


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [Report.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [Report.newInstance] factory method to
 * create an instance of this fragment.
 */
class Report : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private var viewModel: DetectorViewModel? = null
    private var list4Report: List<ResultWiFi>? = null

    private lateinit var viewFragment: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        viewFragment = inflater.inflate(R.layout.fragment_report, container, false)

        viewModel = ViewModelProviders.of(this).get(DetectorViewModel::class.java)
        viewModel!!.getResultScanLiveData().observe(this, Observer {
            list4Report = it
        })

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

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        /* if (context is OnFragmentInteractionListener) {
             listener = context
         } else {
             throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
         }*/
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
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
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Report.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                Report().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
