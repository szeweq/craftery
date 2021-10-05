package szeweq.craftery.mcdata;

import java.util.Arrays;
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
	REGISTRY(Source.FORGE, "registries/IForgeRegistry"),
	TAG(Source.MINECRAFT, "tags/ITag"),
	WORLDGEN_FEATURE(Source.MINECRAFT, "world/gen/feature/Feature"),
	ENCHANTMENT(Source.MINECRAFT, "enchantment/Enchantment"),
	FOOD(Source.MINECRAFT, "item/Food"),
	UNKNOWN(Source.UNKNOWN, "");

	public final Source source;
	public final String type;

	ResourceType(Source src, String typ) {
		source = src;
		type = typ;
	}

	public boolean isCompatible(String typename) {
		var tn = typename.substring(source.pkg.length());
		var b = tn.equals(typename);
		if (!b) switch (this) {
			case REGISTRY: return tn.equals("registries/ForgeRegistry");
			case TAG: return tn.equals("tags/Itag$INamedTag") || tn.equals("tags/Tag");
		}
		return b;
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

	public static final Map<Source, List<ResourceType>> bySource = Arrays.stream(values())
			.filter(x -> x.source != Source.UNKNOWN)
			.collect(Collectors.groupingBy(x -> x.source));
}
