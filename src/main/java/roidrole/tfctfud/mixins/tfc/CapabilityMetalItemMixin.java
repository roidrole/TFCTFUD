package roidrole.tfctfud.mixins.tfc;

import com.google.common.base.CaseFormat;
import net.dries007.tfc.api.capability.metal.CapabilityMetalItem;
import net.dries007.tfc.api.capability.metal.MetalItemHandler;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.oredict.OreDictionary;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import roidrole.tfctfud.utils.IngredientMap;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Supplier;

@Mixin(CapabilityMetalItem.class)
public abstract class CapabilityMetalItemMixin {
	@Mutable
	@Shadow(remap = false)
	@Final
	public static Map<IIngredient<ItemStack>, Supplier<ICapabilityProvider>> CUSTOM_METAL_ITEMS;

	@Inject(
		method = "<clinit>",
		at = @At(
			value = "TAIL",
			remap = false
		),
		remap = false
	)
	private static void moreEfficientHashMap(CallbackInfo ci){
		CUSTOM_METAL_ITEMS = new IngredientMap<>();
	}

	/**
	 * @author roidrole
	 * @reason use IngredientMap for efficiency
	 */
	@Overwrite(remap = false)
	@Nullable
	public static ICapabilityProvider getCustomMetalItem(ItemStack stack) {
		Supplier<ICapabilityProvider> supplier = ((IngredientMap<Supplier<ICapabilityProvider>>) CapabilityMetalItem.CUSTOM_METAL_ITEMS).get(stack);
		if(supplier != null){
			return supplier.get();
		}

		for(int id : OreDictionary.getOreIDs(stack)) {
			ICapabilityProvider handler = getMetalItemFromOreDict(OreDictionary.getOreName(id));
			if (handler != null) {
				return handler;
			}
		}
		return null;
	}

	/**
	 * @author roidrole
	 * @reason only convert one string
	 */
	@Overwrite(remap = false)
	@Nullable
	private static ICapabilityProvider getMetalItemFromOreDict(String oreDict) {
		Metal.ItemType type = null;
		for(Map.Entry<String, Metal.ItemType> entry : CapabilityMetalItem.ORE_DICT_METAL_ITEMS.entrySet()) {
			if (oreDict.startsWith(entry.getKey())) {
				type = entry.getValue();
				oreDict = oreDict.substring(entry.getKey().length());
				break;
			}
		}
		if(type == null){
			return null;
		}
		String oreName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, oreDict);
		if(oreName == null){
			return null;
		}
		Metal metal = TFCRegistries.METALS.getValuesCollection()
			.stream()
			.filter(metalEntry -> oreName.equals(metalEntry.getRegistryName().getPath()))
			.findFirst()
			.orElse(null);

		if(metal == null){
			return null;
		}

		return new MetalItemHandler(metal, type.getSmeltAmount(), true);
	}
}
