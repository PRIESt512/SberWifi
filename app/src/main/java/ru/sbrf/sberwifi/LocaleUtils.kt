package ru.sbrf.sberwifi

import android.annotation.SuppressLint
import org.apache.commons.collections4.Closure
import org.apache.commons.collections4.CollectionUtils
import org.apache.commons.collections4.IterableUtils
import org.apache.commons.collections4.Predicate
import org.apache.commons.lang3.StringUtils
import java.util.*

class LocaleUtils private constructor() {

    init {
        throw IllegalStateException("Utility class")
    }

    private class CountryCodePredicate constructor(countryCode: String) : Predicate<Locale> {
        private val countryCode: String = StringUtils.capitalize(countryCode)

        override fun evaluate(locale: Locale): Boolean {
            return countryCode == locale.country
        }
    }

    private class CountriesPredicate constructor(private val countryCodes: Set<String>) : Predicate<Locale> {

        override fun evaluate(locale: Locale): Boolean {
            return countryCodes.contains(locale.country)
        }
    }

    private class LanguageTagPredicate constructor(private val locale: Locale) : Predicate<Locale> {

        override fun evaluate(`object`: Locale): Boolean {
            return `object`.language == locale.language && `object`.country == locale.country
        }
    }

    private object SyncAvoid {
        @SuppressLint("ConstantLocale")
        internal val DEFAULT = Locale.getDefault()
        internal val COUNTRIES_LOCALES: SortedMap<String, Locale>
        internal val AVAILABLE_LOCALES: List<Locale>
        internal val SUPPORTED_LOCALES: List<Locale>

        init {
            val countryCodes = TreeSet(listOf(*Locale.getISOCountries()))
            val availableLocales = listOf(*Locale.getAvailableLocales())
            AVAILABLE_LOCALES = ArrayList(CollectionUtils.select(availableLocales, CountriesPredicate(countryCodes)))
            COUNTRIES_LOCALES = TreeMap()
            IterableUtils.forEach(AVAILABLE_LOCALES, CountryClosure())
            SUPPORTED_LOCALES = ArrayList(HashSet(listOf(
                    Locale.GERMAN,
                    Locale.ENGLISH,
                    SPANISH,
                    Locale.FRENCH,
                    Locale.ITALIAN,
                    POLISH,
                    PORTUGUESE,
                    RUSSIAN,
                    Locale.SIMPLIFIED_CHINESE,
                    Locale.TRADITIONAL_CHINESE, DEFAULT)))
        }

        private class CountryClosure : Closure<Locale> {
            override fun execute(locale: Locale) {
                val countryCode = locale.country
                COUNTRIES_LOCALES[StringUtils.capitalize(countryCode)] = locale
            }
        }
    }

    companion object {
        internal val SPANISH = Locale("es")
        internal val POLISH = Locale("pl")
        internal val PORTUGUESE = Locale("pt")
        internal val RUSSIAN = Locale("ru")
        private const val SEPARATOR = "_"

        @JvmStatic
        public fun findByCountryCode(countryCode: String): Locale {
            return find(SyncAvoid.AVAILABLE_LOCALES, CountryCodePredicate(countryCode))
        }

        @JvmStatic
        public fun getAllCountries(): List<Locale> {
            return ArrayList(SyncAvoid.COUNTRIES_LOCALES.values)
        }

        @JvmStatic
        fun findByLanguageTag(languageTag: String): Locale {
            return find(SyncAvoid.SUPPORTED_LOCALES, LanguageTagPredicate(fromLanguageTag(languageTag)))
        }

        @JvmStatic
        val supportedLanguages: List<Locale>
            get() = SyncAvoid.SUPPORTED_LOCALES

        @JvmStatic
        val defaultCountryCode: String
            get() = SyncAvoid.DEFAULT.country

        @JvmStatic
        val defaultLanguageTag: String
            get() = LocaleUtils.toLanguageTag(SyncAvoid.DEFAULT)

        private fun find(locales: List<Locale>, predicate: Predicate<Locale>): Locale {
            val result = IterableUtils.find(locales, predicate)
            return result ?: SyncAvoid.DEFAULT
        }

        fun toLanguageTag(locale: Locale): String {
            return locale.language + SEPARATOR + locale.country
        }

        private fun fromLanguageTag(languageTag: String): Locale {
            val codes = languageTag.split(SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (codes.size == 1) {
                return Locale(codes[0])
            }
            return if (codes.size == 2) {
                Locale(codes[0], StringUtils.capitalize(codes[1]))
            } else SyncAvoid.DEFAULT
        }
    }
}
