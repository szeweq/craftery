package szewek.craftery.lookup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import szewek.craftery.mcdata.ScanInfo

class ListAllTags: ModLookup<Pair<String, Set<String>>>("Tags") {
    override val explain = "Table displays all found tags"
    override val itemHeight = 48.dp

    @Composable
    override fun decorate(item: Pair<String, Set<String>>) {
        Column(Modifier.padding(2.dp)) {
            Text(item.first, fontWeight = FontWeight.Bold)
            Text("Values: " + item.second)
        }
    }

    override fun gatherItems(si: ScanInfo): List<Pair<String, Set<String>>> {
        return si.tags.entries.map { (k, v) -> k to v }
    }
}