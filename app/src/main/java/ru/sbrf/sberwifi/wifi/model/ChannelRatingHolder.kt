package ru.sbrf.sberwifi.wifi.model

import androidx.recyclerview.widget.RecyclerView
import ru.sbrf.sberwifi.databinding.ChannelRatingDetailsBinding

class ChannelRatingHolder(private val binding: ChannelRatingDetailsBinding) : RecyclerView.ViewHolder(binding.root) {

    public fun bind(channelRating: ChannelRatingModel) {
        binding.setChannelRating(channelRating)
        binding.executePendingBindings()
    }
}