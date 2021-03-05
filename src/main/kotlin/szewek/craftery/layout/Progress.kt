package szewek.craftery.layout

import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

class ProgressState {
    var value by mutableStateOf(Float.MAX_VALUE)

    fun isActive() = value <= 1f

    fun isIndeterminate() = value == -1f

    fun setIndeterminate() {
        value = -1f
    }

    fun setFinished() {
        value = Float.MAX_VALUE
    }

    fun setFraction(a: Long, b: Long) {
        value = a.toFloat() / b
    }
}

@Composable
fun LinearIndicator(progressState: ProgressState, modifier: Modifier = Modifier) {
    if (progressState.isIndeterminate()) {
        LinearProgressIndicator(modifier)
    } else {
        LinearProgressIndicator(progressState.value, modifier)
    }
}