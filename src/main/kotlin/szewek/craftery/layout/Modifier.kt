package szewek.craftery.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.*
import java.awt.event.MouseEvent

/**
 * Uses [hoverState] to draw a [shape] with [color].
 */
fun Modifier.hover(
    color: Color,
    shape: Shape = RectangleShape
) = composed {
    val (hover, setHover) = remember { mutableStateOf(false) }
    val mod = hoverState(setHover)
    if (hover) mod.background(color, shape) else mod
}

/**
 * Updates hover state when pointer enters or exits the component.
 */
fun Modifier.hoverState(cb: (Boolean) -> Unit) = pointerMoveFilter(
    onEnter = { cb(true); false },
    onExit = { cb(false); false }
)

/**
 * Checks which mouse button was pressed while filtering pointer input events.
 */
fun Modifier.clickableNumbered(vararg buttons: Int, onClick: (Int) -> Unit) = pointerInput(Unit) {
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