package szeweq.craftery.layout

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import szeweq.craftery.mcdata.Model
import szeweq.craftery.mcdata.Models

@Composable
fun ItemSlot(name: String = "minecraft:item/golden_shovel", count: Int = 0, modifier: Modifier = Modifier) {
    val model = remember(name) { mutableStateOf<Model>(Model.Empty) }
    LaunchedEffect(name) {
        val m = Models.getModelOf(name)
        model.value = m
    }

    Box(modifier.size(48.dp)) {
        Canvas(Modifier.size(48.dp).padding(4.dp).border(2.dp, MaterialTheme.colors.primary)) {
            model.value.draw(this)
        }
        if (count > 1) {
            Text("$count", Modifier.padding(4.dp).align(Alignment.BottomEnd))
        }
    }
}

@Composable
fun CraftingGrid() = Column {
    for (i in 0 until 3) Row {
        for (j in 0 until 3) ItemSlot("minecraft:item/chest", 3)
    }
}
