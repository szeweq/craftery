package szeweq.craftery.util

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import com.fasterxml.jackson.core.type.TypeReference
import szeweq.craftery.net.Downloader
import java.util.concurrent.CompletableFuture

/**
 * Method for binding specific values for use in an event.
 */
fun <T> MutableState<T>.bind(value: T): () -> Unit = { this.value = value }

fun <T> ((T) -> Unit).bind(value: T): () -> Unit = { this(value) }

@Composable
fun <T> rememberInitialState(value: T): MutableState<T> = remember { mutableStateOf(value) }

infix fun (() -> Unit).and(other: () -> Unit): () -> Unit = {
    this()
    other()
}

/**
 * Method fix for Kotlin code.
 * Kotlin compiler tried to use [java.lang.Object] class while specific class is required.
 */
inline fun <reified T> downloadJson(url: String, progress: LongBiConsumer): CompletableFuture<T> =
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
