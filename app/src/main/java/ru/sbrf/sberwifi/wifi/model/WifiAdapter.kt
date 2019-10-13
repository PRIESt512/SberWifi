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

class WifiAdapter(fragment: Fragment, context: Context, val wifiDetails: List<WiFiDetail> = ArrayList(15), resource: Int = R.layout.wifi_list_menu) :
        ArrayAdapter<WiFiDetail>(context, resource, wifiDetails) {

    private val channelRating: ChannelRating = ChannelRating()
    private val viewModel = ViewModelProviders.of(fragment).get(DetectorViewModel::class.java)

    init {
        viewModel.resultScanLiveData.observe(fragment, Observer {
            this.clear()
            fragment.tag
            this.addAll(it.getWiFiDetails())
            this.notifyDataSetChanged()
            channelRating.setWiFiDetails(it.getWiFiDetails())
        })
    }

    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var viewHolder: ViewHolder? = null
        var view = convertView
        if (view == null) {
            view = INSTANCE.getLayoutInflater().inflate(R.layout.wifi_list_menu, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder?
        }
        val wifiDetail = getItem(position)!!

        viewHolder!!.levelImage.setImageResource(wifiDetail.wiFiSignal.strength.imageResource())
        viewHolder.levelImage.setColorFilter(ContextCompat.getColor(context, wifiDetail.wiFiSignal.strength.colorResource()))

        val strengthWiFi = wifiDetail.wiFiSignal.strength
        viewHolder.level.setTextColor(ContextCompat.getColor(context, strengthWiFi.colorResource()))
        viewHolder.level.text = String.format("%ddBm", wifiDetail.wiFiSignal.level)

        val security = wifiDetail.security
        viewHolder.capabilities.setCompoundDrawablesWithIntrinsicBounds(security.imageResource, 0, 0, 0)
        viewHolder.capabilities.text = wifiDetail.capabilities

        viewHolder.ssid_and_mac.text = wifiDetail.title
        viewHolder.channel.text = String.format(Locale.ENGLISH, "%d", wifiDetail.wiFiSignal.primaryWiFiChannel.channel)
        viewHolder.primaryFrequency.text = String.format(Locale.ENGLISH, "%d МГц |", wifiDetail.wiFiSignal.primaryWiFiChannel.frequency)
        viewHolder.channel_frequency_range.text = String.format(Locale.ENGLISH, "%d - %d", wifiDetail.wiFiSignal.frequencyStart, wifiDetail.wiFiSignal.frequencyEnd)
        viewHolder.width.text = String.format(Locale.ENGLISH, "(%d%s)", wifiDetail.wiFiSignal.wiFiWidth.frequencyWidth, WiFiSignal.FREQUENCY_UNITS)

        val strength = Strength.reverse(channelRating.getStrength(wifiDetail.wiFiSignal.primaryWiFiChannel))
        val size = Strength.values().size
        viewHolder.channel_rating.max = size
        viewHolder.channel_rating.numStars = size
        viewHolder.channel_rating.isIndicator
        viewHolder.channel_rating.rating = (strength.ordinal + 1).toFloat()
        val color = ContextCompat.getColor(context, strength.colorResource())
        viewHolder.channel_rating.progressTintList = ColorStateList.valueOf(color)

        return view!!
    }

    private inner class ViewHolder(val levelImage: ImageView, val level: TextView, val capabilities: TextView,
                                   val ssid_and_mac: TextView, val channel: TextView, val primaryFrequency: TextView,
                                   val channel_frequency_range: TextView, val width: TextView, val channel_rating: RatingBar) {
        constructor(view: View) : this(
                view.findViewById<ImageView>(R.id.levelImage), view.findViewById<TextView>(R.id.level),
                view.findViewById<TextView>(R.id.capabilities), view.findViewById<TextView>(R.id.ssid_and_mac),
                view.findViewById<TextView>(R.id.channel), view.findViewById<TextView>(R.id.primaryFrequency),
                view.findViewById<TextView>(R.id.channel_frequency_range), view.findViewById<TextView>(R.id.width),
                view.findViewById<RatingBar>(R.id.channel_rating))
    }
}