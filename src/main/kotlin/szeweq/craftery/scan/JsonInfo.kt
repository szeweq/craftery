package szeweq.craftery.scan

import com.fasterxml.jackson.databind.JsonNode
import szeweq.craftery.mcdata.DataResourceType

class JsonInfo(val name: String, val namespace: String, val type: DataResourceType) {
    val details = mutableMapOf<String, String>()

    fun gatherDetails(obj: JsonNode) {
        when (type) {
            DataResourceType.RECIPE -> {
                val s = obj["type"]?.asText()
                if (s != null) {
                    details["Type"] = s
                }
            }
            else -> {}
        }
    }
}