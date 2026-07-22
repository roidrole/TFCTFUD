package roidrole.tfctfud;

import com.cleanroommc.configanytime.ConfigAnytime;
import net.minecraftforge.common.config.Config;

@Config(modid = Tags.MOD_ID)
public class TFCTFUDConfig {
	public static boolean optimizeLeafDecay = true;
	public static boolean optimizeCapabilities = true;
	public static boolean optimizeOreGen = true;

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

	static {
		ConfigAnytime.register(TFCTFUDConfig.class);
	}
}
