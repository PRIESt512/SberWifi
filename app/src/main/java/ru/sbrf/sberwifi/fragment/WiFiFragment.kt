package ru.sbrf.sberwifi.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.AbsListView
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import ru.sbrf.sberwifi.R
import ru.sbrf.sberwifi.view.ParallaxScrollListView
import ru.sbrf.sberwifi.wifi.model.WiFiData
import ru.sbrf.sberwifi.wifi.model.WiFiDetail
import ru.sbrf.sberwifi.wifi.model.WifiAdapter

// the fragment initialization parameters
private const val VIEW_MODEL_PARAM = "listWiFi"

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

    private var myContext: FragmentActivity? = null

    private var list: ParallaxScrollListView? = null

    private var wifiAdapter: WifiAdapter? = null

    private val json = Json(JsonConfiguration.Stable)

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

        val header = LayoutInflater.from(this.context).inflate(R.layout.listview_header, null)
        val mImageView = header.findViewById<ImageView>(R.id.layout_header_image)

        list = viewFragment.findViewById(R.id.layout_listwifi)
        list?.setZoomRatio(ParallaxScrollListView.ZOOM_X2)
        list?.setParallaxImageView(mImageView)
        list?.addHeaderView(header)

        val savedListWiFi = savedInstanceState?.getString(VIEW_MODEL_PARAM)
        wifiAdapter = if (savedListWiFi != null) {
            val wifi = json.parse(WifiSave.serializer(), savedListWiFi)
            WifiAdapter(this, context!!, wifi.listWifi)
        } else {
            WifiAdapter(this, context!!)
        }

        list?.adapter = wifiAdapter

        list?.setOnItemClickListener { _, _, position, _ ->
            val transaction = myContext?.supportFragmentManager?.beginTransaction()!!
            val fragment = DetailInfoFragment.newInstance(json.stringify(WiFiDetail.serializer(), wifiAdapter?.getItem(position - 1)!!))
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        list?.setOnScrollListener(object : AbsListView.OnScrollListener {

            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
            }

            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                when (view?.id) {
                    R.id.layout_listwifi -> {
                        val lastItem = firstVisibleItem + visibleItemCount
                        if ((lastItem == totalItemCount)) {
                            val context = list?.context
                            val controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down) as LayoutAnimationController
                            list?.layoutAnimation = controller
                            list?.scheduleLayoutAnimation()
                        }
                    }
                }
            }
        })
        return viewFragment
    }

    fun setOnWiFiListener(callback: OnWifiInteractionListener) {
        this.callback = callback
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        myContext = when (context) {
            is FragmentActivity -> {
                context
            }
            else -> throw RuntimeException("Не удалось получить основной контекст")
        }
        if (context is OnWifiInteractionListener) {
            callback = context
        } else {
            throw RuntimeException("$context must implement OnReportInteractionListener")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (wifiAdapter?.wifiDetails?.isEmpty()!!)
            return
        val report = json.stringify(WifiSave.serializer(), WifiSave(wifiAdapter?.wifiDetails!!))
        outState.putString(VIEW_MODEL_PARAM, report)
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    interface OnWifiInteractionListener {
        fun onFragmentInteraction(wiFiDetail: WiFiDetail)
    }

    @Serializable
    private class WifiSave(val listWifi: List<WiFiDetail>)

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
