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

fun Modifier.hover(
    color: Color,
    shape: Shape = RectangleShape
) = composed {
    val (hover, setHover) = remember { mutableStateOf(false) }
    val mod = hoverState(setHover)
    if (hover) mod.background(color, shape) else mod
}

fun Modifier.hoverState(cb: (Boolean) -> Unit) = pointerMoveFilter(
    onEnter = { cb(true); false },
    onExit = { cb(false); false }
)

fun Modifier.clickableNumbered(button: Int, onClick: () -> Unit) = pointerInput(Unit) {
    forEachGesture {
        awaitPointerEventScope {
            var event: PointerEvent
            do {
                event = awaitPointerEvent()
            } while (!event.changes.all { it.changedToDown() })
            event.changes.forEach { it.consumeDownChange() }
            if (event.mouseEvent?.button == button) {
                val up = waitForUpOrCancellation()
                if (up != null) {
                    up.consumeDownChange()
                    onClick()
                }
            }
        }
    }
}