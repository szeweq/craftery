package szeweq.craftery.lookup

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.fasterxml.jackson.core.JsonParseException
import szeweq.craftery.scan.ScanInfo

class ParseErrors: ModLookup<Pair<String, Exception>>("Parse errors") {
    override val explain = "Exceptions thrown during parsing process"

    @Composable
    override fun ColumnScope.decorate(item: Pair<String, Exception>) {
        Text(item.first, fontWeight = FontWeight.Bold)
        Text(item.second.message ?: item.second.javaClass.name + " (No message provided)")
    }

    override fun gatherItems(si: ScanInfo) = si.parseExceptions.toList()
}