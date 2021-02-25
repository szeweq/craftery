package szewek.craftery.layout

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import szewek.craftery.mcdata.Models

@Composable
fun ItemSlot(name: String = "minecraft:item/golden_shovel", count: Int = 0) {
    val scope = rememberCoroutineScope()
    val img = remember { mutableStateOf(emptySkijaImage) }
    scope.launch { Models.getImageOf(name)?.let { img.value = it } }
    Box(Modifier.border(2.dp, MaterialTheme.colors.primary, MaterialTheme.shapes.medium)) {
        Image(img.value.asImageBitmap(), name, Modifier.size(32.dp))
    }
}

@Composable
fun CraftingGrid() {

}