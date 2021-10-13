package szeweq.craftery.views

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import szeweq.craftery.layout.*

object About: View("About") {

    @Composable
    override fun content() {
        CenteredColumn(ModifierMaxSize) {
            TextH5("Craftery", Modifier.padding(8.dp))
            Text("© 2020-2021 Szeweq", Modifier.padding(bottom = 8.dp))
            val uriHandler = LocalUriHandler.current
            Button(onClick = {
                uriHandler.openUri("https://github.com/Szeweq/craftery")
            }, content = ComposeScopeText("Github"))
        }
    }
}