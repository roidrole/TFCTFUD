package roidrole.tfctfud;

import com.cleanroommc.configanytime.ConfigAnytime;
import net.minecraftforge.common.config.Config;

@Config(modid = Tags.MOD_ID)
public class TFCTFUDConfig {
	@Config.Comment("Replaces the HashSet used as a visited list with a boolean[], being faster and more memory-efficient.")
	public static boolean optimizeLeafDecay = true;

	@Config.Comment("Optimizes TFC's capability gathering algorithm with a more efficient one.")
	public static boolean optimizeCapabilities = true;

	@Config.Comment("Replaces the algorithm to place ores in cluster with a more performant one. Pretty invasive mixin.")
	public static boolean optimizeOreGen = true;

	@Config.Comment("TFC creates a new entity every time to check its spawning chance, taking ≈ 6% of total worldgen time in larger packs")
	public static boolean optimizeAnimalGen = true;

	@Config.Comment("Prevents TFC from placing empty loose rocks if enableLooseRocks is false in TFC's config.")
	public static boolean dontPlaceEmptyTiles = true;

	@Config.Comment("Makes the calendar shut up about errors. This does not fix the errors, only silences them.")
	public static boolean calendarShutUp = false;

	@Config.Comment({
		"Item size strings use a resource location, allowing packmakers to edit them",
		"Translation keys are \"tfc.capability.weight.[lowercase default name]\" and \"tfc.capability.size.[lowercase default name]\"",
		"Defaults are provided."
	})
	public static boolean itemSizeLocalization = false;

	@Config.Comment("If not empty, the jei category for knapping will only show the provided stone type. Format : \"tfc:andesite\"")
	public static String knappingShowOneRockType = "";

	@Config.Comment({
		"By default, TFC will show a green overlay over any item that can rot, including immediately-rotten ones",
		"This can be undesirable for some items, especially modded or immediately-rotting ones.",
		"Any item id in this list (format 'minecraft:rotten_flesh') will not have that overlay"
	})
	public static String[] rottingOverlayBlacklist = { };

	static {
		ConfigAnytime.register(TFCTFUDConfig.class);
	}
}
