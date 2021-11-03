package szeweq.craftery.mcdata;

public enum DataResourceType {
	BLOCK_STATE("blockstates"),
	ITEM_MODEL("models/item"),
	BLOCK_MODEL("models/block"),
	ITEM_TEXTURE("textures/item"),
	BLOCK_TEXTURE("textures/block"),
	RECIPE("recipes"),
	LOOT_TABLE("loot_tables"),
	ADVANCEMENT("advancements"),
	TRANSLATION("lang"),
	ITEM_TAG("tags/items"),
	BLOCK_TAG("tags/blocks"),
	FLUID_TAG("tags/fluids"),
	ENTITY_TYPE_TAG("tags/entity_types"),
	PARTICLE("particles"),
	SHADER("shaders"),
	STRUCTURE("structures"),
	UNKNOWN("");

	private final String dir;

	DataResourceType(String dir) {
		this.dir = dir;
	}

	public boolean isTagType() {
		return dir.startsWith("tags/");
	}

	private static final DataResourceType[] ASSETS = {
			BLOCK_STATE, ITEM_MODEL, BLOCK_MODEL, ITEM_TEXTURE, BLOCK_TEXTURE, TRANSLATION,
			PARTICLE, SHADER
	};
	private static final DataResourceType[] DATA = {
			RECIPE, LOOT_TABLE, ADVANCEMENT, ITEM_TAG, BLOCK_TAG, FLUID_TAG, ENTITY_TYPE_TAG,
			STRUCTURE
	};

	public static DataResourceType detect(String mainDir, String path) {
		return switch (mainDir) {
			case "assets" -> dirMatch(path, ASSETS);
			case "data" -> dirMatch(path, DATA);
			default -> UNKNOWN;
		};
	}

	private static DataResourceType dirMatch(String path, DataResourceType[] drts) {
		for (DataResourceType drt : drts) {
			final String dir = drt.dir;
			if (path.startsWith(dir) && path.charAt(dir.length()) == '/') {
				return drt;
			}
		}
		return UNKNOWN;
	}
}
