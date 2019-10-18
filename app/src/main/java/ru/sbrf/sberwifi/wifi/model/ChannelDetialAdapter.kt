package ru.sbrf.sberwifi.wifi.model

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import ru.sbrf.sberwifi.R
import ru.sbrf.sberwifi.databinding.ChannelRatingDetailsBinding
import ru.sbrf.sberwifi.livemodel.DetectorViewModel

class ChannelDetialAdapter(fragment: Fragment) : RecyclerView.Adapter<ChannelRatingHolder>() {

    private val channelRating: ChannelRating = ChannelRating()

    private val viewModel = ViewModelProviders.of(fragment).get(DetectorViewModel::class.java)

    private val items = ArrayList<ChannelRatingModel>(15)

    init {
        viewModel.resultScanLiveData.observe(fragment, Observer {
            channelRating.setWiFiDetails(it.getWiFiDetails())
            val strength = Strength.reverse(channelRating.getStrength(wifiDetail.wiFiSignal.primaryWiFiChannel))

            it.getWiFiDetails()[0].wiFiSignal.primaryWiFiChannel.channel
        })
    }

    public fun setData(data: List<ChannelRatingModel>) {
        items.clear()
        items.addAll(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelRatingHolder {
        val inflater = LayoutInflater.from(parent.context)
        val channelRatingItem = inflate<ChannelRatingDetailsBinding>(inflater, R.layout.channel_rating_details, parent, false)
        return ChannelRatingHolder(channelRatingItem)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ChannelRatingHolder, position: Int) {
        holder.bind(items[position])
    }

}