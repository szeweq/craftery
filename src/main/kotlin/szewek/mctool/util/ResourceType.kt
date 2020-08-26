package szewek.mctool.util

enum class ResourceType(val typ: String) {
    BLOCK("net/minecraft/block/Block"),
    ITEM("net/minecraft/item/Item"),
    TILE_ENTITY_TYPE("net/minecraft/tileentity/TileEntityType"),
    CONTAINER_TYPE("net/minecraft/inventory/container/ContainerType"),
    POI_TYPE("net/minecraft/village/PointOfInterestType"),
    VILLAGER_PROFESSION("net/minecraft/entity/merchant/villager/VillagerProfession"),
    ITEM_GROUP("net/minecraft/item/ItemGroup")
}