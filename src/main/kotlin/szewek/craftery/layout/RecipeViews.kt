package szewek.craftery.layout

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import szewek.craftery.mcdata.Model
import szewek.craftery.mcdata.Models

@Composable
fun ItemSlot(name: String = "minecraft:item/golden_shovel", count: Int = 0) {
    val scope = rememberCoroutineScope()
    val model = remember(name) {
        val x = mutableStateOf<Model>(Model.Empty)
        scope.launch { Models.getModelOf(name).let { x.value = it } }
        x
    }

    Box(Modifier.border(2.dp, MaterialTheme.colors.primary)) {
        Canvas(Modifier.size(48.dp).padding(4.dp)) {
            model.value.draw(this)
        }
    }
}

@Composable
fun CraftingGrid() = Column {
    for (i in 0 until 3) Row {
        for (j in 0 until 3) ItemSlot("minecraft:item/chest")
    }
}