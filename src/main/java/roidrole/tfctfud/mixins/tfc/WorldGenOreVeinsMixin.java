package roidrole.tfctfud.mixins.tfc;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.dries007.tfc.world.classic.worldgen.WorldGenOreVeins;
import net.dries007.tfc.world.classic.worldgen.vein.IVeinExpansion;
import net.dries007.tfc.world.classic.worldgen.vein.Vein;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Stream;

@Mixin(value = WorldGenOreVeins.class, remap = false)
public abstract class WorldGenOreVeinsMixin {
	//Micro-optimization to avoid creating an intermediary list
	@Redirect(
		method = "getVeinsAtChunk",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/stream/Stream;collect(Ljava/util/stream/Collector;)Ljava/lang/Object;"
		)
	)
	private static Object tfctfud_directInsertion(Stream<Vein> instance, Collector<Vein, ?, List<Vein>> arCollector, @Local(ordinal = 0, argsOnly = true) List<Vein> listToAdd){
		instance.forEach(listToAdd::add);
		return Collections.emptyList();
	}
	//We want to delegate generation of the vein in a chunk to the vein itself. We proceed in two steps

	//1. We kill the geneation loop
	@ModifyConstant(
		method = "generate",
		constant = @Constant(intValue = 24)
	)
	private static int tfctfud_noLooping(int constant){
		return 8;
	}

	//2. We inject vein.tfctfud_generate
	@Inject(
		method = "generate",
		at = @At(
			value = "INVOKE",
			target = "Lnet/dries007/tfc/world/classic/worldgen/vein/Vein;getType()Lnet/dries007/tfc/world/classic/worldgen/vein/VeinType;",
			ordinal = 0
		)
	)
	private static void tfctfud_generate(
		Random random,
		int chunkX, int chunkZ, World world,
		IChunkGenerator chunkGenerator,
		IChunkProvider chunkProvider,
		CallbackInfo ci,
		@Local(name = "vein") Vein vein,
		@Local(name = "generated") LocalBooleanRef generated,
		@Local(name = "chunkBlockPos") BlockPos chunkBlockPos
	){
		generated.set(((IVeinExpansion)vein).tfctfud_generate(world, chunkBlockPos, random));
	}
}
