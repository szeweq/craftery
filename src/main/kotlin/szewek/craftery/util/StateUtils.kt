package szewek.craftery.util

import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.ImageBitmap

fun <T> MutableState<T>.bindValue(value: T): () -> Unit = { this.value = value }

val emptyImage = ImageBitmap(1, 1)