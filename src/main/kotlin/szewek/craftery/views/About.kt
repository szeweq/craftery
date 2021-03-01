package szewek.craftery.views

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import szewek.craftery.layout.CenteredColumn
import szewek.craftery.layout.View

class About: View("About") {

    @Composable
    override fun content() {
        CenteredColumn(Modifier.fillMaxSize(1.0f)) {
            Text("Craftery", Modifier.padding(8.dp), fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("© 2020-2021 Szewek")
        }
    }
}