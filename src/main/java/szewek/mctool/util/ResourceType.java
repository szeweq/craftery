package szewek.mctool.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum ResourceType {
	BLOCK(Source.MINECRAFT, "block/Block"),
	ITEM(Source.MINECRAFT, "item/Item"),
	TILE_ENTITY_TYPE(Source.MINECRAFT, "tileentity/TileEntityType"),
	CONTAINER_TYPE(Source.MINECRAFT, "inventory/container/ContainerType"),
	POI_TYPE(Source.MINECRAFT, "village/PointOfInterestType"),
	VILLAGER_PROFESSION(Source.MINECRAFT, "entity/merchant/villager/VillagerProfession"),
	ITEM_GROUP(Source.MINECRAFT, "item/ItemGroup"),
	CAPABILITY(Source.FORGE, "common/capabilities/Capability"),
	REGISTRY(Source.FORGE, "registries/IForgeRegistry");

	public final Source source;
	public final String type;

	ResourceType(Source src, String typ) {
		source = src;
		type = typ;
	}

	public enum Source {
		MINECRAFT("net/minecraft/"),
		FORGE("net/minecraftforge/"),
		UNKNOWN("");

		public final String pkg;

		Source(String pkg) {
			this.pkg = pkg;
		}
	}

	public static final Map<Source, List<ResourceType>> bySource = Arrays.stream(values()).collect(Collectors.groupingBy(x -> x.source));
}
