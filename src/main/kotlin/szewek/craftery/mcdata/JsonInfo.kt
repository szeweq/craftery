package szewek.craftery.mcdata

import javax.json.JsonObject

class JsonInfo(val name: String, val namespace: String, val type: DataResourceType) {
    val details = mutableMapOf<String, String>()

    fun gatherDetails(obj: JsonObject) {
        when (type) {
            DataResourceType.RECIPE -> {
                obj.getString("type", null)?.apply { details["Type"] = this }
            }
            else -> {}
        }
    }
}