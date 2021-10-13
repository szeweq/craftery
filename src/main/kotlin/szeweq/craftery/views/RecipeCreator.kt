package szeweq.craftery.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import szeweq.craftery.layout.*
import szeweq.craftery.mcdata.MinecraftData
import szeweq.craftery.mcdata.Models

class RecipeCreator: View("Create recipes") {
    private val exampleVanillaItems = arrayOf(
        "cobblestone",
        "dirt",
        "oak_log",
        "diamond_pickaxe",
        "netherite_sword",
        "ender_pearl",
        "furnace"
    ).map { "minecraft:item/$it" }

    init {
        viewScope.launch {
            MinecraftData.loadAllFilesFromJar(null)
            Models.compile()
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun content() {
        if (Models.compileState) {
            Box {
                Row(ModifierMaxSize) {
                    val mod = Modifier.padding(8.dp)
                    Column(mod) {
                        CraftingGrid()
                    }
                    Column(mod) {
                        val hoverBg = MaterialTheme.colors.onSurface.copy(0.2f)
                        LazyVerticalGrid(GridCells.Adaptive(52.dp)) {
                            items(exampleVanillaItems) {
                                Box { ItemSlot(it, modifier = Modifier.hover(hoverBg)) }
                            }
                        }
                    }
                }
            }
        } else Box(ModifierMaxSize, contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val mod = Modifier.padding(2.dp)
                CircularProgressIndicator(mod)
                Text("Please wait...", mod)
            }
        }
    }
}
