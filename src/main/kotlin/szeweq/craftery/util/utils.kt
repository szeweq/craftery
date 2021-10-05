package szeweq.craftery.util

import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.ImageBitmap
import com.fasterxml.jackson.core.type.TypeReference
import szeweq.craftery.net.Downloader

/**
 * Method for binding specific values for use in an event.
 */
fun <T> MutableState<T>.bindValue(value: T): () -> Unit = { this.value = value }

/**
 * Method fix for Kotlin code.
 * Kotlin compiler tried to use [java.lang.Object] class while specific class is required.
 */
inline fun <reified T> downloadJson(url: String, progress: LongBiConsumer): T? =
    Downloader.downloadJson(url, object : TypeReference<T>() {}, progress)

/**
 * Declaration of an "empty" 1x1 image.
 */
val emptyImage = ImageBitmap(1, 1)

inline fun logTime(name: String, fn: () -> Unit) {
    val d = System.nanoTime()
    fn()
    TimeLogManager.logNano(name, d)
}