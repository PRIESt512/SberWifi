package ru.sbrf.sberwifi.util

import android.content.res.Resources
import androidx.annotation.RawRes
import org.apache.commons.lang3.StringUtils

class FileUtils private constructor() {
    init {
        throw IllegalStateException("Utility class")
    }

    companion object {

        fun readFile(resources: Resources, @RawRes id: Int): String {
            try {
                resources.openRawResource(id).use { inputStream ->
                    val size = inputStream.available()
                    val bytes = ByteArray(size)
                    val count = inputStream.read(bytes)
                    return if (count != size) {
                        StringUtils.EMPTY
                    } else String(bytes)
                }
            } catch (e: Exception) {
                // file is corrupted
                return StringUtils.EMPTY
            }

        }
    }
}