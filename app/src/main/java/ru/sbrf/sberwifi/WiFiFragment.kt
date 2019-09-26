package ru.sbrf.sberwifi

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

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
    private var viewModel: DetectorViewModel? = null

    private var list4Report: List<ResultWiFi>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            list4Report = it.getParcelableArrayList(VIEW_MODEL_PARAM)
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val viewFragment = inflater.inflate(R.layout.fragment_wifi, container, false)

        list = viewFragment.findViewById(R.id.layout_listwifi)
        list?.setZoomRatio(ParallaxScrollListView.ZOOM_X2)

        val header = LayoutInflater.from(this.context).inflate(R.layout.listview_header, null)
        val mImageView = header.findViewById(R.id.layout_header_image) as ImageView

        list?.setParallaxImageView(mImageView)
        list?.addHeaderView(header)

        viewModel = ViewModelProviders.of(this).get(DetectorViewModel::class.java)

        viewModel!!.getResultScanLiveData().observe(this, Observer {
            val listWiFi = ArrayList<ItemListWifi>()

            for (item in it) {
                listWiFi.add(ItemListWifi(item.scan.SSID, item.scan.level))
            }

            val adapter = ArrayAdapter(this.context!!,
                    android.R.layout.simple_expandable_list_item_1,
                    listWiFi)

            list?.adapter = adapter
        })

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

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    interface OnWifiInteractionListener {
        fun onFragmentInteraction(uri: Uri)
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
        fun newInstance(data: ArrayList<ResultWiFi>) =
                WiFiFragment().apply {
                    arguments = Bundle().apply {
                        putParcelableArrayList(VIEW_MODEL_PARAM, data)
                    }
                }
    }
}
