package szeweq.craftery.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.TextStyle
import com.fasterxml.jackson.core.type.TypeReference
import szeweq.craftery.net.Downloader
import szeweq.desktopose.core.LongBiConsumer
import java.util.concurrent.CompletableFuture

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

@Composable
@ReadOnlyComposable
infix fun ProvidableCompositionLocal<TextStyle>.providesMerged(value: TextStyle) =
    this provides this.current.merge(value)

@Composable
@ReadOnlyComposable
infix fun <T> ProvidableCompositionLocal<T>.providesFrom(other: CompositionLocal<T>) =
    this provides other.current