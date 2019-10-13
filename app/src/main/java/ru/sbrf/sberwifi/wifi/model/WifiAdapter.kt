package ru.sbrf.sberwifi.wifi.model

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import ru.sbrf.sberwifi.MainContext.INSTANCE
import ru.sbrf.sberwifi.R
import ru.sbrf.sberwifi.livemodel.DetectorViewModel
import java.util.*
import kotlin.collections.ArrayList

class WifiAdapter(fragment: Fragment, context: Context, wifiDetails: List<WiFiDetail> = ArrayList(15), resource: Int = R.layout.wifi_list_menu) :
        ArrayAdapter<WiFiDetail>(context, resource, wifiDetails) {

    private val channelRating: ChannelRating = ChannelRating()
    private val viewModel = ViewModelProviders.of(fragment).get(DetectorViewModel::class.java)

    init {
        viewModel.getResultScanLiveData().observe(fragment, Observer {
            this.clear()
            this.addAll(it.getWiFiDetails())
            channelRating.setWiFiDetails(it.getWiFiDetails())
        })
    }

    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val wifiDetail = getItem(position)!!

        var view: View? = convertView
        if (view == null) {
            view = INSTANCE.getLayoutInflater().inflate(R.layout.wifi_list_menu, parent, false)
        }

        val imageView = view?.findViewById<ImageView>(R.id.levelImage)
        imageView?.setImageResource(wifiDetail.wiFiSignal.strength.imageResource())
        imageView?.setColorFilter(ContextCompat.getColor(context, wifiDetail.wiFiSignal.strength.colorResource()))

        val strengthWiFi = wifiDetail.wiFiSignal.strength
        val textLevel = view?.findViewById<TextView>(R.id.level)
        textLevel?.setTextColor(ContextCompat.getColor(context, strengthWiFi.colorResource()))
        textLevel?.text = String.format("%ddBm", wifiDetail.wiFiSignal.level)

        val security = wifiDetail.security
        val securityImage = view?.findViewById<ImageView>(R.id.securityImage)
        securityImage?.setImageResource(security.imageResource)

        view?.findViewById<TextView>(R.id.capabilities)?.text = wifiDetail.capabilities
        view?.findViewById<TextView>(R.id.ssid_and_mac)?.text = String.format(Locale.ENGLISH, "%s (%s)", wifiDetail.ssid, wifiDetail.bssid)
        view?.findViewById<TextView>(R.id.channel)?.text = String.format(Locale.ENGLISH, "%d", wifiDetail.wiFiSignal.primaryWiFiChannel.channel)
        view?.findViewById<TextView>(R.id.primaryFrequency)?.text = String.format(Locale.ENGLISH, "%d МГц |", wifiDetail.wiFiSignal.primaryWiFiChannel.frequency)
        view?.findViewById<TextView>(R.id.channel_frequency_range)?.text = String.format(Locale.ENGLISH, "%d - %d", wifiDetail.wiFiSignal.frequencyStart, wifiDetail.wiFiSignal.frequencyEnd)
        view?.findViewById<TextView>(R.id.width)?.text = String.format(Locale.ENGLISH, "(%d%s)", wifiDetail.wiFiSignal.wiFiWidth.frequencyWidth, WiFiSignal.FREQUENCY_UNITS)

        val strength = Strength.reverse(channelRating.getStrength(wifiDetail.wiFiSignal.primaryWiFiChannel))
        val ratingBar = view?.findViewById<RatingBar>(R.id.channel_rating)
        val size = Strength.values().size
        ratingBar?.max = size
        ratingBar?.numStars = size
        ratingBar?.isIndicator
        ratingBar?.rating = (strength.ordinal + 1).toFloat()
        val color = ContextCompat.getColor(context, strength.colorResource())
        ratingBar?.progressTintList = ColorStateList.valueOf(color)

        return view!!
    }
}