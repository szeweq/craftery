package szewek.craftery.scan

import com.google.gson.JsonObject
import szewek.craftery.mcdata.DataResourceType

class JsonInfo(val name: String, val namespace: String, val type: DataResourceType) {
    val details = mutableMapOf<String, String>()

    fun gatherDetails(obj: JsonObject) {
        when (type) {
            DataResourceType.RECIPE -> {
                val s = obj["type"]?.asString
                if (s != null) {
                    details["Type"] = s
                }
            }
            else -> {}
        }
    }
}