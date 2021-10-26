package szeweq.craftery.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.input.pointer.*
import szeweq.craftery.util.rememberInitialState

val ModifierMaxSize = Modifier.fillMaxSize()

/**
 * Checks which mouse button was pressed while filtering pointer input events.
 */
fun Modifier.clickableNumbered(vararg buttons: Int, onClick: (Int) -> Unit) = pointerInput(onClick) {
    forEachGesture {
        awaitPointerEventScope {
            var event: PointerEvent
            do {
                event = awaitPointerEvent()
            } while (!event.changes.all { it.changedToDown() })
            event.changes.forEach { it.consumeDownChange() }
            val bn = event.mouseEvent?.button ?: 0
            if (bn > 0 && bn in buttons) {
                val up = waitForUpOrCancellation()
                if (up != null) {
                    up.consumeDownChange()
                    onClick(bn)
                }
            }
        }
    }
}