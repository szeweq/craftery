package szeweq.craftery.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.input.pointer.*

val ModifierMaxSize = Modifier.fillMaxSize()

/**
 * Uses [hoverState] to draw a [shape] with [color].
 */
fun Modifier.hover(
    color: Color = Color.Unspecified,
    shape: Shape = RectangleShape
) = composed {
    val (hover, setHover) = remember { mutableStateOf(false) }
    val mod = hoverState(setHover)
    if (hover) mod.background(color.takeOrElse { LocalHoverColor.current }, shape) else mod
}

/**
 * Updates hover state when pointer enters or exits the component.
 */
fun Modifier.hoverState(cb: (Boolean) -> Unit) = pointerInput(cb) {
    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent()
            when (event.type) {
                PointerEventType.Enter -> {
                    cb(true)
                }
                PointerEventType.Exit -> {
                    cb(false)
                }
            }
        }
    }
}

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