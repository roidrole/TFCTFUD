package roidrole.tfctfud.mixins.tfc;

import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import roidrole.tfctfud.utils.IngredientMap;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Supplier;

@Mixin(CapabilityItemHeat.class)
public abstract class CapabilityItemHeatMixin {
	@Mutable
	@Shadow(remap = false)
	@Final
	public static Map<IIngredient<ItemStack>, Supplier<ICapabilityProvider>> CUSTOM_ITEMS;

	@Inject(
		method = "<clinit>",
		at = @At(
			value = "TAIL",
			remap = false
		),
		remap = false
	)
	private static void moreEfficientHashMap(CallbackInfo ci){
		CUSTOM_ITEMS = new IngredientMap<>();
	}


	/**
	 * @author roidrole
	 * @reason use IngredientMap for efficiency
	 */
	@Overwrite(remap = false)
	@Nullable
	public static ICapabilityProvider getCustomHeat(ItemStack stack) {
		Supplier<ICapabilityProvider> supplier = ((IngredientMap<Supplier<ICapabilityProvider>>)CapabilityItemHeat.CUSTOM_ITEMS).get(stack);
		if(supplier == null){
			return null;
		}
		return supplier.get();
	}
}
