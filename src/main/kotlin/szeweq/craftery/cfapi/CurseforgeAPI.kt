package szeweq.craftery.cfapi

import com.fasterxml.jackson.core.type.TypeReference

object CurseforgeAPI {

    private inline fun <reified T> getJson(path: String, params: List<Pair<String, Any>> = listOf()): T =
        CFAPIClient.getJson(object : TypeReference<T>() {}, path, params)

    fun findAddons(query: String, type: Int) =
        getJson<Array<AddonSearch>>(
            "addon/search",
            listOf("gameId" to 432, "sectionId" to type, "searchFilter" to query)
        )

    fun getAddonFiles(addonId: Int) =
        getJson<Array<AddonFile>>("addon/$addonId/files")

    fun downloadURL(addon: Int, file: Int): String =
        CFAPIClient.getString("addon/$addon/file/$file/download-url", listOf())

}