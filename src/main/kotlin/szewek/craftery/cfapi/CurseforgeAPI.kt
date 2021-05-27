package szewek.craftery.cfapi

import com.google.gson.reflect.TypeToken

object CurseforgeAPI {

    private inline fun <reified T> getJson(path: String, params: List<Pair<String, Any>> = listOf()) =
        CFAPIClient.getJson(object : TypeToken<T>() {}, path, params)

    fun findAddons(query: String, type: Int) =
        getJson<Array<AddonSearch>>("addon/search", listOf("gameId" to 432, "sectionId" to type, "searchFilter" to query))

    fun getAddonFiles(addonId: Int) = getJson<Array<AddonFile>>("addon/$addonId/files")

    fun downloadURL(addon: Int, file: Int) = CFAPIClient.getString("addon/$addon/file/$file/download-url", listOf())

}