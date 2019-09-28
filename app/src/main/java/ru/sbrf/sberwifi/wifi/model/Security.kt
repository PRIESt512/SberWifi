package ru.sbrf.sberwifi.wifi.model

import org.apache.commons.collections4.IterableUtils
import org.apache.commons.collections4.Predicate
import ru.sbrf.sberwifi.R
import ru.sbrf.sberwifi.util.EnumUtils
import java.util.*

enum class Security private constructor(val imageResource: Int) {
    NONE(R.drawable.ic_lock_open),
    WPS(R.drawable.ic_lock_outline),
    WEP(R.drawable.ic_lock_outline),
    WPA(R.drawable.ic_lock),
    WPA2(R.drawable.ic_lock);

    private class SecurityPredicate constructor(private val securities: List<Security>) : Predicate<Security> {

        override fun evaluate(security: Security): Boolean {
            return securities.contains(security)
        }
    }

    companion object {

        fun findAll(capabilities: String?): List<Security> {
            val results = TreeSet<Security>()
            if (capabilities != null) {
                val values = capabilities.toUpperCase(Locale.getDefault())
                        .replace("][", "-").replace("]", "").replace("[", "").split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (value in values) {
                    try {
                        results.add(Security.valueOf(value))
                    } catch (e: Exception) {
                        // skip getCapabilities that are not getSecurity
                    }

                }
            }
            return ArrayList(results)
        }

        fun findOne(capabilities: String): Security {
            val result = IterableUtils.find(EnumUtils.values(Security::class.java), SecurityPredicate(findAll(capabilities)))
            return result ?: NONE
        }
    }

}
