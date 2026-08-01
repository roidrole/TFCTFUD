package roidrole.tfctfud.mixins.tfc;

import com.llamalad7.mixinextras.sugar.Local;
import mezz.jei.api.IModRegistry;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipe;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.compat.jei.TFCJEIPlugin;
import net.dries007.tfc.compat.jei.wrappers.KnappingRecipeWrapper;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import roidrole.tfctfud.TFCTFUD;
import roidrole.tfctfud.TFCTFUDConfig;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@Mixin(TFCJEIPlugin.class)
public abstract class TFCJEIPluginMixin {
	@Redirect(
		method = "register",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/stream/Stream;flatMap(Ljava/util/function/Function;)Ljava/util/stream/Stream;"
		),
		remap = false
	)
	private Stream<KnappingRecipeWrapper> onlyShowOneRecipeType(
		Stream<KnappingRecipe> instance,
		Function<KnappingRecipe, Stream<KnappingRecipeWrapper>> function,
		@Local(argsOnly = true, name = "arg1") IModRegistry registry
	){
		//Embed the null check in an Optional so it's still effectively final for the lambda below
		final Rock selectedRock = Optional.ofNullable(TFCRegistries.ROCKS.getValue(new ResourceLocation(TFCTFUDConfig.knappingShowOneRockType))).orElseGet(() -> {
			//The supplier is allowed to print this as it's only ever called if the value is null.
			TFCTFUD.LOGGER.error(new NullPointerException("Configured rock type "+TFCTFUDConfig.knappingShowOneRockType +" is invalid! Falling back to the first registered rock"));
			return TFCRegistries.ROCKS.iterator().next();
		});

		//Originally did a flatMap on the rock registry. Let us not
		return instance.map(
			recipe -> new KnappingRecipeWrapper.Stone(recipe, registry.getJeiHelpers().getGuiHelper(), selectedRock)
		);
	}
}
