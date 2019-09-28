package ru.sbrf.sberwifi.vendor.model

import org.apache.commons.lang3.StringUtils
import java.util.*
import kotlin.math.min

class VendorUtils private constructor() {

    init {
        throw IllegalStateException("Utility class")
    }

    companion object {
        val MAX_SIZE = 6
        private val SEPARATOR = ":"

        fun clean(macAddress: String?): String {
            if (macAddress == null) {
                return StringUtils.EMPTY
            }
            val result = macAddress.replace(SEPARATOR, "")
            val locale = Locale.getDefault()
            return result.substring(0, min(result.length, MAX_SIZE)).toUpperCase(locale)
        }

        fun toMacAddress(source: String?): String {
            if (source == null) {
                return StringUtils.EMPTY
            }
            return if (source.length < MAX_SIZE) {
                "*$source*"
            } else source.substring(0, 2) +
                    "$SEPARATOR + ${source.substring(2, 4)})" +
                    "$SEPARATOR + ${source.substring(4, 6)}"
        }
    }

}