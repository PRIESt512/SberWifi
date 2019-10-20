package ru.sbrf.sberwifi.wifi.model

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sbrf.sberwifi.MainContext
import ru.sbrf.sberwifi.R
import ru.sbrf.sberwifi.databinding.ChannelRatingDetailsBinding
import ru.sbrf.sberwifi.livemodel.DetectorViewModel
import ru.sbrf.sberwifi.predicate.WiFiBandPredicate
import ru.sbrf.sberwifi.wifi.band.WiFiBand
import ru.sbrf.sberwifi.wifi.band.WiFiChannel

class ChannelDetailAdapter(fragment: Fragment, private val bestChannels: TextView, val context: Context, isGHZ5: Boolean) : RecyclerView.Adapter<ChannelRatingHolder>() {


    private val channelRating: ChannelRating = ChannelRating()

    private val viewModel = ViewModelProviders.of(fragment).get(DetectorViewModel::class.java)

    private val items = ArrayList<ChannelRatingModel>(15)

    private val wiFiBand = if (isGHZ5) WiFiBand.GHZ5 else WiFiBand.GHZ2

    private val wiFiChannels = wiFiBand.wiFiChannels.getAvailableChannels(countryCode)

    private val mainScope = MainScope()

    init {
        viewModel.resultScanLiveData.observe(fragment, Observer {
            mainScope.launch {
                updateViewFragment(it)
            }
        })
        mainScope.launch {
            updateViewFragment(MainContext.INSTANCE.wiFiData)
        }
    }

    private suspend fun updateViewFragment(wiFiData: WiFiData) = withContext(Dispatchers.Default) {
        val wiFiDetails = wiFiData.getWiFiDetails(WiFiBandPredicate(wiFiBand))
        channelRating.setWiFiDetails(wiFiDetails)
        bestChannels(wiFiBand, wiFiChannels)

        setData(wiFiChannels.map { item ->
            val strength = Strength.reverse(channelRating.getStrength(item))
            ChannelRatingModel((strength.getNumberOfStrength()).toFloat(), item.channel.toString(), channelRating.getCount(item).toString())
        }.toList())

        mainScope.launch { this@ChannelDetailAdapter.notifyDataSetChanged() }
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

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ChannelRatingHolder, position: Int) {
        holder.bind(items[position])
    }

    private fun bestChannels(wiFiBand: WiFiBand, wiFiChannels: List<WiFiChannel>) {
        val channelAPCounts = channelRating.getBestChannels(wiFiChannels)
        var channelCount = 0
        val result = StringBuilder()
        for (channelAPCount in channelAPCounts) {
            if (channelCount > MAX_CHANNELS_TO_DISPLAY) {
                result.append("...")
                break
            }
            if (result.isNotEmpty()) {
                result.append(", ")
            }
            result.append(channelAPCount.wiFiChannel.channel)
            channelCount++
        }
        if (result.isNotEmpty()) {
            mainScope.launch {
                bestChannels.text = result.toString()
                bestChannels.setTextColor(ContextCompat.getColor(context, R.color.success))
            }
        } else {
            val resources = context.resources
            val message = StringBuilder(resources.getText(R.string.channel_rating_best_none))
            if (WiFiBand.GHZ2 == wiFiBand) {
                message.append(resources.getText(R.string.channel_rating_best_alternative))
                message.append(" ")
                message.append(context.resources.getString(WiFiBand.GHZ5.textResource))
            }
            mainScope.launch {
                bestChannels.text = message
                bestChannels.setTextColor(ContextCompat.getColor(context, R.color.error))
            }
        }
    }

    companion object {
        private const val MAX_CHANNELS_TO_DISPLAY = 10
        private const val countryCode = "RU"

    }
}