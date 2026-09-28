package roidrole.tfctfud;

import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TFCTFUDMixinLoader implements ILateMixinLoader {
	@Override
	public List<String> getMixinConfigs() {
		ArrayList<String> mixinConfigs = new ArrayList<>(4);
		if(TFCTFUDConfig.calendarShutUp){
			addMixinConf(mixinConfigs, "calendar_shut_up");
		}
		if(TFCTFUDConfig.itemSizeLocalization){
			addMixinConf(mixinConfigs, "itemsize_localization");
		}
		if(!TFCTFUDConfig.knappingShowOneRockType.isEmpty()){
			addMixinConf(mixinConfigs, "knapping_show_one_stone");
		}
		if(TFCTFUDConfig.optimizeCapabilities){
			addMixinConf(mixinConfigs, "optimize_capability");
		}
		if(TFCTFUDConfig.optimizeOreGen){
			addMixinConf(mixinConfigs, "optimize_ore_gen");
		}
		if(TFCTFUDConfig.optimizeLeafDecay){
			addMixinConf(mixinConfigs, "optimize_leaf_decay");
		}
		if(TFCTFUDConfig.rottingOverlayBlacklist.length != 0){
			addMixinConf(mixinConfigs, "rotting_overlay_blacklist");
		}
		if(TFCTFUDConfig.dontPlaceEmptyTiles){
			addMixinConf(mixinConfigs, "worldgen_loose_disable");
		}
		if(TFCTFUDConfig.optimizeAnimalGen){
			addMixinConf(mixinConfigs, "worldgen_animals");
		}
		if(TFCTFUDConfig.optimizeFallingBlocks){
			addMixinConf(mixinConfigs, "optimize_falling_blocks");
		}
		if(TFCTFUDConfig.optimizeChunkGenGaussian){
			addMixinConf(mixinConfigs, "optimize_worldgen_gaussian");
		}
		return mixinConfigs;
	}
	private static void addMixinConf(final Collection<String> mixinConfigs, final String mixin){
		mixinConfigs.add("mixins." + Tags.MOD_ID + '.' + mixin + ".json");
	}
}
