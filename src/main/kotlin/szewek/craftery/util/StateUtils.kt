package szewek.craftery.util

import androidx.compose.runtime.MutableState

fun <T> MutableState<T>.bindValue(value: T): () -> Unit = { this.value = value }