package roidrole.tfctfud.mixins.tfc;

import net.dries007.tfc.api.capability.food.CapabilityFood;
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

@Mixin(CapabilityFood.class)
public abstract class CapabilityFoodMixin {
	@Mutable
	@Shadow(remap = false)
	@Final
	public static Map<IIngredient<ItemStack>, Supplier<ICapabilityProvider>> CUSTOM_FOODS;

	@Inject(
		method = "<clinit>",
		at = @At(
			value = "TAIL",
			remap = false
		),
		remap = false
	)
	private static void moreEfficientHashMap(CallbackInfo ci){
		CUSTOM_FOODS = new IngredientMap<>();
	}


	/**
	 * @author roidrole
	 * @reason use IngredientMap for efficiency
	 */
	@Overwrite(remap = false)
	@Nullable
	public static ICapabilityProvider getCustomFood(ItemStack stack) {
		Supplier<ICapabilityProvider> supplier = ((IngredientMap<Supplier<ICapabilityProvider>>) CapabilityFood.CUSTOM_FOODS).get(stack);
		if(supplier == null){
			return null;
		}
		return supplier.get();
	}
}
