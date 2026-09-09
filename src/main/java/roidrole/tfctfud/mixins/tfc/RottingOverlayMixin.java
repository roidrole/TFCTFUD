package roidrole.tfctfud.mixins.tfc;

import net.dries007.tfc.client.ClientRegisterEvents;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import roidrole.tfctfud.TFCTFUDConfig;

import java.util.function.Predicate;
import java.util.stream.Stream;

@Mixin(ClientRegisterEvents.class)
public abstract class RottingOverlayMixin {

	@Unique
	private static final Item[] tfctfud_overlayBlacklist = new Item[TFCTFUDConfig.rottingOverlayBlacklist.length];
	static {
		for (int i = 0; i < TFCTFUDConfig.rottingOverlayBlacklist.length; i++) {
			tfctfud_overlayBlacklist[i] = ForgeRegistries.ITEMS.getValue(new ResourceLocation(TFCTFUDConfig.rottingOverlayBlacklist[i]));
		}
	}

	@Redirect(
		method = "registerColorHandlerItems",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/stream/Stream;filter(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;",
			ordinal = 1
		),
		remap = false
	)
	private static Stream<Item> skipRottenOverlay(Stream<Item> instance, Predicate<? super Item> predicate){
		return instance.filter(item -> predicate.test(item) && !ArrayUtils.contains(tfctfud_overlayBlacklist, item));
	}
}
