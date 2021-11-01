package szeweq.kt

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun <K, V> Map<K, V>.entryPairFlow(): Flow<Pair<K, V>> = flow {
    for ((k, v) in this@entryPairFlow) {
        emit(Pair(k, v))
    }
}