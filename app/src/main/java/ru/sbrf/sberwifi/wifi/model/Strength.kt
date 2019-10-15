package ru.sbrf.sberwifi.wifi.model

import ru.sbrf.sberwifi.R

enum class Strength constructor(private val imageResource: Int, private val colorResource: Int) {
    ZERO(R.drawable.ic_signal_wifi_0_bar, R.color.error),
    ONE(R.drawable.ic_signal_wifi_1_bar, R.color.warning),
    TWO(R.drawable.ic_signal_wifi_2_bar, R.color.warning),
    THREE(R.drawable.ic_signal_wifi_3_bar, R.color.success),
    FOUR(R.drawable.ic_signal_wifi_4_bar, R.color.success);

    fun colorResource(): Int {
        return colorResource
    }

    fun colorResourceDefault(): Int {
        return R.color.regular
    }

    fun imageResource(): Int {
        return imageResource
    }

    fun weak(): Boolean {
        return ZERO == this
    }

    companion object {

        fun calculate(level: Int): Strength {
            val index = WiFiUtils.calculateSignalLevel(level, values().size)
            return values()[index]
        }

        fun reverse(strength: Strength): Strength {
            val index = Strength.values().size - strength.ordinal - 1
            return Strength.values()[index]
        }
    }
}