package ru.sbrf.sberwifi.util

import android.content.res.Resources
import android.util.Log
import androidx.annotation.RawRes
import org.apache.commons.lang3.StringUtils
import java.io.InputStream

class FileUtils private constructor() {
    init {
        throw IllegalStateException("Utility class")
    }

    companion object {

        @JvmStatic
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

        fun getInputStream(resources: Resources, @RawRes id: Int): InputStream? {
            try {
                return resources.openRawResource(id)
            } catch (e: Exception) {
                // file is corrupted
                Log.e("FileUtils", e.message!!)
            }
            return null
        }
    }
}