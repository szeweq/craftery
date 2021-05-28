package szewek.craftery.util

import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.ImageBitmap

fun <T> MutableState<T>.bindValue(value: T): () -> Unit = { this.value = value }

inline fun <reified T> downloadJson(url: String, progress: LongBiConsumer): T? =
    Downloader.downloadJson(url, T::class.java, progress)

val emptyImage = ImageBitmap(1, 1)