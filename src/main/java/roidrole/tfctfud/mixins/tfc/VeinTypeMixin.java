package roidrole.tfctfud.mixins.tfc;

import com.llamalad7.mixinextras.sugar.Local;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.world.classic.worldgen.vein.TFCTFUDVeinCluster;
import net.dries007.tfc.world.classic.worldgen.vein.Vein;
import net.dries007.tfc.world.classic.worldgen.vein.VeinType;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(VeinType.class)
public abstract class VeinTypeMixin {
	@Inject(
		method = "createVein",
		at = @At(
			value = "NEW",
			target = "net/dries007/tfc/world/classic/worldgen/vein/VeinCluster"
		),
		cancellable = true,
		remap = false
	)
	private void tfctfud_getTFCTFUD_cluster(
		Random rand,
		int chunkX,
		int chunkZ,
		CallbackInfoReturnable<Vein> cir,
		@Local(name = "startPos") BlockPos startPos,
		@Local(name = "grade")Ore.Grade grade
	){
		cir.setReturnValue(new TFCTFUDVeinCluster(startPos, (VeinType)(Object) this, grade, rand));
	}
}
