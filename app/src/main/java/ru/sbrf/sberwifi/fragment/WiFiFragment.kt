package ru.sbrf.sberwifi.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import ru.sbrf.sberwifi.R
import ru.sbrf.sberwifi.view.ParallaxScrollListView
import ru.sbrf.sberwifi.wifi.model.WiFiData
import ru.sbrf.sberwifi.wifi.model.WiFiDetail
import ru.sbrf.sberwifi.wifi.model.WifiAdapter

// the fragment initialization parameters
private const val VIEW_MODEL_PARAM = "listData"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [WiFiFragment.OnWifiInteractionListener] interface
 * to handle interaction events.
 * Use the [WiFiFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WiFiFragment : Fragment() {
    private var callback: OnWifiInteractionListener? = null

    private var list: ParallaxScrollListView? = null

    private var wifiAdapter: WifiAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            //list4Report = it.getParcelableArrayList(VIEW_MODEL_PARAM)
        }
    }


    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val viewFragment = inflater.inflate(R.layout.fragment_wifi, container, false)

        list = viewFragment.findViewById(R.id.layout_listwifi)
        list?.setZoomRatio(ParallaxScrollListView.ZOOM_X2)
        val rt = savedInstanceState?.getString("1")
        rt?.let {
            val json = Json(JsonConfiguration.Stable)
            val wifi = json.parse(WiFiDetail.serializer(), it)
        }

        val header = LayoutInflater.from(this.context).inflate(R.layout.listview_header, null)
        val mImageView = header.findViewById(R.id.layout_header_image) as ImageView

        list?.setParallaxImageView(mImageView)
        list?.addHeaderView(header)

        wifiAdapter = WifiAdapter(this, context!!)
        list?.adapter = wifiAdapter
        return viewFragment
    }

    fun setOnWiFiListener(callback: OnWifiInteractionListener) {
        this.callback = callback
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnWifiInteractionListener) {
            callback = context
        } else {
            throw RuntimeException("$context must implement OnReportInteractionListener")
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val json = Json(JsonConfiguration.Stable)
        if (wifiAdapter?.wifiDetails?.isEmpty()!!)
            return
        val report = json.stringify(WiFiDetail.serializer(), wifiAdapter?.wifiDetails!![0])
        outState.putString("1", report)
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    interface OnWifiInteractionListener {
        fun onFragmentInteraction(wiFiDetail: WiFiDetail)
    }

    private class ItemListWifi(val SSID: String, val level: Int) {
        override fun toString(): String {
            return String.format("SSID: %s, Level: %sdBm", SSID, level)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param data list data scan WiFi
         * @return A new instance of fragment Wifi.
         */
        @JvmStatic
        fun newInstance(data: WiFiData) =
                WiFiFragment().apply {
                    arguments = Bundle().apply {
                        //putParcelable(VIEW_MODEL_PARAM, data)
                    }
                }

        @JvmStatic
        fun newInstance() = WiFiFragment()
    }
}
