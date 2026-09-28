package roidrole.tfctfud.mixins.tfc;

import net.dries007.tfc.world.classic.ChunkGenTFC;
import net.dries007.tfc.world.classic.CustomChunkPrimer;
import net.minecraft.world.chunk.ChunkPrimer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import roidrole.tfctfud.utils.Utils;

import java.util.Random;

@Mixin(value = ChunkGenTFC.class, remap = false)
public abstract class ChunkGenTFCMixin {

	@Shadow
	private float rainfall;

	@Unique
	private float tfctfud_grassProb;
	@Unique
	private float tfctfud_sandProb;

	@Inject(
		method = "replaceBlocksForBiomeHigh",
		at = @At("HEAD")
	)
	private void computeChances(int chunkX, int chunkZ, ChunkPrimer inp, CustomChunkPrimer outp, CallbackInfo ci){
		tfctfud_grassProb = (float) Utils.normalCdf((rainfall - 150.0) / 1.3);
		tfctfud_sandProb = (float) Utils.normalCdf((75.0 - rainfall) / 1.3);
	}

	@Redirect(
		method = "replaceBlocksForBiomeHigh",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/Random;nextGaussian()D",
			ordinal = 0
		)
	)
	private double replaceGaussianGrass(Random instance){
		boolean passes = instance.nextFloat() < tfctfud_grassProb;
		//Rainfall is between 0 and 500. We just need to provide a value that works with that
		if(passes){
			return -512.0D;
		} else {
			return 512.0D;
		}
	}
	@Redirect(
		method = "replaceBlocksForBiomeHigh",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/Random;nextGaussian()D",
			ordinal = 1
		)
	)
	private double replaceGaussianSand(Random instance){
		boolean passes = instance.nextFloat() < tfctfud_sandProb;
		//Rainfall is between 0 and 500. We just need to provide a value that works with that
		if(passes){
			return -512.0D;
		} else {
			return 512.0D;
		}
	}
}
