package szewek.craftery.mcdata

import com.google.gson.JsonObject

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