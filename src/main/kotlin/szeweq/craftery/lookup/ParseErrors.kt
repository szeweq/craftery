package szeweq.craftery.lookup

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.flow.Flow
import szeweq.craftery.scan.ScanInfo
import szeweq.kt.entryPairFlow

class ParseErrors: ModLookup<Pair<String, Exception>>("Parse errors") {
    override val explain = "Exceptions thrown during parsing process"

    @Composable
    override fun ColumnScope.decorate(item: Pair<String, Exception>) {
        Text(item.first, fontWeight = FontWeight.Bold)
        Text(item.second.message ?: (item.second.javaClass.name + " (No message provided)"))
    }

    override fun gatherItems(si: ScanInfo): Flow<Pair<String, Exception>> =
        si.parseExceptions.entryPairFlow()
}