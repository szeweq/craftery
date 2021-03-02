package szewek.craftery.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import szewek.craftery.layout.*
import szewek.craftery.mcdata.MinecraftData
import szewek.craftery.mcdata.Models

class RecipeCreator: View("Create recipes") {

    init {
        GlobalScope.launch {
            MinecraftData.loadAllFilesFromJar(null)
            Models.compile()
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun content() {
        if (Models.compileState) {
            Row {
                val mod = Modifier.padding(8.dp)
                Column(mod) {
                    CraftingGrid()
                }
                Column(mod) {
                    LazyVerticalGrid(GridCells.Adaptive(56.dp)) {
                        items(15) {
                            Column {
                                ItemSlot()
                                Text("Golden shovel")
                            }
                        }
                    }
                }
            }

        } else Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val mod = Modifier.padding(2.dp)
                CircularProgressIndicator(mod)
                Text("Please wait...", mod)
            }
        }
    }
}
