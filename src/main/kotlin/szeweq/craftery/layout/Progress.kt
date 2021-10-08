package szeweq.craftery.layout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * A state object for displaying and updating values in process-related tasks (like downloading a file).
 */
open class ProgressState {
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

class MessageProgressState: ProgressState() {
    var message by mutableStateOf("")
}

@Composable
fun LinearIndicator(
    progressState: ProgressState,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.primary
) {
    if (progressState.isIndeterminate()) {
        LinearProgressIndicator(modifier, color)
    } else {
        LinearProgressIndicator(progressState.value, modifier, color)
    }
}

@Composable
fun ProgressCard(
    msgProgress: MessageProgressState,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.primary
) = Card(modifier) { Column {
    Text(msgProgress.message, Modifier.padding(8.dp))
    LinearIndicator(msgProgress, Modifier.fillMaxWidth(), color = color)
} }