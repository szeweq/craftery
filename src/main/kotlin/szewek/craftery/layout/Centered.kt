package szewek.craftery.layout

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun CenteredRow(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) =
    Row(modifier, Arrangement.Center, Alignment.CenterVertically, content)

@Composable
fun CenteredColumn(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) =
    Column(modifier, Arrangement.Center, Alignment.CenterHorizontally, content)