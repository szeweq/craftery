package szewek.craftery.views

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import szewek.craftery.layout.CenteredColumn
import szewek.craftery.layout.ComposeText
import szewek.craftery.layout.View

object About: View("About") {
    private val fillSizeModifier = Modifier.fillMaxSize(1.0f)

    @Composable
    override fun content() {
        produceState(null, key1 = 0) {}
        CenteredColumn(fillSizeModifier) {
            Text("Craftery", Modifier.padding(8.dp), fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("© 2020-2021 Szewek", Modifier.padding(bottom = 8.dp))
            val uriHandler = LocalUriHandler.current
            Button(onClick = {
                uriHandler.openUri("https://github.com/Szewek/craftery")
            }, content = ComposeText("Github"))
        }
    }
}