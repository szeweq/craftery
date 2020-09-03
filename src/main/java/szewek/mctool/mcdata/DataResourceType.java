package szewek.mctool.mcdata;

public enum DataResourceType {
	BLOCK_STATE("blockstates"),
	ITEM_MODEL("models/item"),
	BLOCK_MODEL("models/block"),
	ITEM_TEXTURE("textures/item"),
	BLOCK_TEXTURE("textures/block"),
	RECIPE("recipes"),
	LOOT_TABLE("loot_tables"),
	TRANSLATION("lang"),
	ITEM_TAG("tags/items"),
	BLOCK_TAG("tags/blocks"),
	FLUID_TAG("tags/fluids"),
	UNKNOWN("");

	private final String dir;

	DataResourceType(String dir) {
		this.dir = dir;
	}


	static DataResourceType detect(String mainDir, String path) {
		switch (mainDir) {
			case "assets":
				return dirMatch(path, BLOCK_STATE, ITEM_MODEL, BLOCK_MODEL, ITEM_TEXTURE, BLOCK_TEXTURE, TRANSLATION);
			case "data":
				return dirMatch(path, RECIPE, LOOT_TABLE, ITEM_TAG, BLOCK_TAG, FLUID_TAG);
		}
		return UNKNOWN;
	}

	private static DataResourceType dirMatch(String path, DataResourceType ...drts) {
		for (DataResourceType drt : drts) {
			final String dir = drt.dir;
			if (path.startsWith(dir) && path.charAt(dir.length()) == '/') {
				return drt;
			}
		}
		return UNKNOWN;
	}
}
