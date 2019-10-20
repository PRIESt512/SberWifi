package ru.sbrf.sberwifi.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import ru.sbrf.sberwifi.FragmentEnum
import ru.sbrf.sberwifi.MainContext
import ru.sbrf.sberwifi.R
import ru.sbrf.sberwifi.wifi.model.ChannelDetailAdapter
import ru.sbrf.sberwifi.wifi.model.WiFiDetail
import ru.sbrf.sberwifi.wifi.model.WiFiSignal
import java.util.*

private const val VIEW_MODEL_PARAM = "wifiChannel"


/**
 * A simple [Fragment] subclass.
 * Use the [DetailInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailInfoFragment : Fragment() {

    private val json = Json(JsonConfiguration.Stable)

    private lateinit var wifiDetail: WiFiDetail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            wifiDetail = json.parse(WiFiDetail.serializer(), it.getString(VIEW_MODEL_PARAM)!!)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(FragmentEnum.DetailInfoFragment.idFragment, container, false)

        view.findViewById<TextView>(R.id.ssid).text = wifiDetail.SSID
        view.findViewById<TextView>(R.id.mac).text = wifiDetail.bssid

        view.findViewById<TextView>(R.id.central_frequency).text =
                String.format(Locale.ENGLISH, "%d Мгц", wifiDetail.wiFiSignal.primaryFrequency)

        view.findViewById<TextView>(R.id.channel_frequency_range).text =
                String.format(Locale.ENGLISH, "%d Мгц - %d Мгц", wifiDetail.wiFiSignal.frequencyStart, wifiDetail.wiFiSignal.frequencyEnd)

        view.findViewById<TextView>(R.id.channel).text = wifiDetail.wiFiSignal.channelDisplay

        view.findViewById<TextView>(R.id.bandwidth).text =
                String.format(Locale.ENGLISH, "%d%s", wifiDetail.wiFiSignal.wiFiWidth.frequencyWidth, WiFiSignal.FREQUENCY_UNITS)

        view.findViewById<TextView>(R.id.type_wifi).text =
                if (wifiDetail.wiFiSignal.wiFiBand.isGHZ5) "5 ГГц" else "2.4 ГГц"

        view.findViewById<TextView>(R.id.distance_wifi).text = wifiDetail.wiFiSignal.distance
        view.findViewById<TextView>(R.id.vendor_wifi).text =
                MainContext.INSTANCE.vendorService.findVendorName(wifiDetail.bssid)

        val viewManager = LinearLayoutManager(this.context)
        val adapterView = ChannelDetailAdapter(this, view.findViewById(R.id.bestChannel), this.context!!, wifiDetail.wiFiSignal.wiFiBand.isGHZ5)
        view.findViewById<RecyclerView>(R.id.channelRatingListDetail).apply {
            setHasFixedSize(true)
            adapter = adapterView
            layoutManager = viewManager
        }
        return view
    }

    override fun onDetach() {
        super.onDetach()
    }

    companion object {
        @JvmStatic
        fun newInstance(wifiChannel: String) =
                DetailInfoFragment().apply {
                    arguments = Bundle().apply {
                        putString(VIEW_MODEL_PARAM, wifiChannel)
                    }
                }
    }
}
