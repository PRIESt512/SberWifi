package ru.sbrf.sberwifi.util

import org.apache.commons.collections4.CollectionUtils
import org.apache.commons.collections4.Predicate
import org.apache.commons.collections4.PredicateUtils
import org.apache.commons.collections4.Transformer
import java.util.*

public class EnumUtils {

    private fun EnumUtils() {
        throw IllegalStateException("Utility class")
    }

    companion object {

        @JvmStatic
        fun <T : Enum<*>> find(enumType: Class<T>, index: Int, defaultValue: T): T {
            val values = enumType.enumConstants
            return if (index < 0 || index >= values!!.size) {
                defaultValue
            } else values[index]
        }

        @JvmStatic
        fun <T : Enum<*>> find(enumType: Class<T>, predicate: Predicate<T>, defaultValue: T): T {
            val results = ArrayList(CollectionUtils.select(values(enumType), predicate))
            return if (results.isEmpty()) defaultValue else results[0]
        }

        @JvmStatic
        fun <T : Enum<*>> find(enumType: Class<T>, ordinals: Set<String>, defaultValue: T): Set<T> {
            val results = HashSet(CollectionUtils.collect(ordinals, ToEnum(enumType, defaultValue)))
            return if (results.isEmpty()) values(enumType) else results
        }

        @JvmStatic
        fun <T : Enum<*>> find(values: Set<T>): Set<String> {
            return HashSet(CollectionUtils.collect(values, ToOrdinal()))
        }

        @JvmStatic
        fun <T : Enum<*>> ordinals(enumType: Class<T>): Set<String> {
            return HashSet(CollectionUtils.collect(values(enumType), ToOrdinal()))
        }

        @JvmStatic
        fun <T : Enum<*>> values(enumType: Class<T>): Set<T> {
            return HashSet(listOf(*enumType.enumConstants!!))
        }

        @JvmStatic
        fun <T : Enum<*>, U> predicate(enumType: Class<T>, input: Collection<T>, transformer: Transformer<T, Predicate<U>>): Predicate<U> {
            return if (input.size >= values(enumType).size) {
                PredicateUtils.truePredicate()
            } else PredicateUtils.anyPredicate(CollectionUtils.collect(input, transformer))
        }

        private class ToEnum<T : Enum<*>> constructor(private val enumType: Class<T>, private val defaultValue: T) : Transformer<String, T> {

            override fun transform(input: String): T? {
                return try {
                    find<T>(enumType, Integer.parseInt(input), defaultValue)
                } catch (e: Exception) {
                    defaultValue
                }

            }
        }

        private class ToOrdinal<T : Enum<*>> : Transformer<T, String> {
            override fun transform(input: T): String {
                return input.ordinal.toString()
            }
        }
    }
}

