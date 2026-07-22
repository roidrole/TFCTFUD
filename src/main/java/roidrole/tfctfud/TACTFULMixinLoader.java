package roidrole.tfctfud;

import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.ArrayList;
import java.util.List;

public class TACTFULMixinLoader implements ILateMixinLoader {
	@Override
	public List<String> getMixinConfigs() {
		ArrayList<String> mixinConfigs = new ArrayList<>(3);
		if(TFCTFUDConfig.calendarShutUp){
			mixinConfigs.add("mixins."+Tags.MOD_ID+".calendar_shut_up.json");
		}
		if(TFCTFUDConfig.itemSizeLocalization){
			mixinConfigs.add("mixins."+Tags.MOD_ID+".itemsize_localization.json");
		}
		if(!TFCTFUDConfig.knappingShowOneRockType.isEmpty()){
			mixinConfigs.add("mixins."+Tags.MOD_ID+".knapping_show_one_stone.json");
		}
		if(TFCTFUDConfig.optimizeCapabilities){
			mixinConfigs.add("mixins."+Tags.MOD_ID+".optimize_capability.json");
		}
		if(TFCTFUDConfig.optimizeOreGen){
			mixinConfigs.add("mixins."+Tags.MOD_ID+".optimize_ore_gen.json");
		}
		if(TFCTFUDConfig.optimizeLeafDecay){
			mixinConfigs.add("mixins."+Tags.MOD_ID+".optimize_leaf_decay.json");
		}
		return mixinConfigs;
	}
}
