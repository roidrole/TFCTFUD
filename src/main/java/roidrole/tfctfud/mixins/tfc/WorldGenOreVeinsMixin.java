package roidrole.tfctfud.mixins.tfc;

import com.llamalad7.mixinextras.sugar.Local;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.world.classic.ChunkGenTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;
import net.dries007.tfc.world.classic.worldgen.WorldGenOreVeins;
import net.dries007.tfc.world.classic.worldgen.vein.IVeinExpansion;
import net.dries007.tfc.world.classic.worldgen.vein.Vein;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static net.dries007.tfc.world.classic.worldgen.WorldGenOreVeins.CHUNK_RADIUS;
import static net.dries007.tfc.world.classic.worldgen.WorldGenOreVeins.getNearbyVeins;

@Mixin(WorldGenOreVeins.class)
public abstract class WorldGenOreVeinsMixin {
	//To avoid creating an intermediary list
	@Redirect(
		method = "getVeinsAtChunk",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/stream/Stream;collect(Ljava/util/stream/Collector;)Ljava/lang/Object;"
		)
	)
	private static Object directInsertion(Stream<Vein> instance, Collector<Vein, ?, List<Vein>> arCollector, @Local(ordinal = 0, argsOnly = true) List<Vein> listToAdd){
		instance.forEach(listToAdd::add);
		return listToAdd;
	}

	/**
	 * @author roidrole
	 * @reason move that to Vein class
	 */
	@Overwrite(remap = false)
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider){

		if (!(chunkGenerator instanceof ChunkGenTFC)) return;
		final BlockPos chunkBlockPos = new BlockPos(chunkX << 4, 0, chunkZ << 4);
		ChunkDataTFC chunkData = ChunkDataTFC.get(world, chunkBlockPos);
		if (!chunkData.isInitialized()) return;
		if (world.provider.getDimension() != 0) return;

		List<Vein> veins = getNearbyVeins(chunkX, chunkZ, world.getSeed(), CHUNK_RADIUS);

		for (Vein vein : veins)
		{
			boolean generated = ((IVeinExpansion)vein).tfctfud_generate(world, chunkBlockPos, random);
			// Chunk post-processing, if a vein generated
			if (vein.getType() != null)
			{
				if (generated)
				{
					chunkData.markVeinGenerated(vein);
				}
				else if (ConfigTFC.General.DEBUG.enable)
				{
					// Failed to generate, debug info
					// This can be by a number of factors, mainly because at each expected replacing position we didn't find a matching raw rock.
					// Some possible causes: Width / Height / Shape / Density / Y / Rock Layer
					TerraFirmaCraft.getLog().debug("Failed to generate vein '{}' in chunk ({}, {}). Vein center pos ({}x, {}y, {}z)", vein.getType().getRegistryName(), chunkX, chunkZ, vein.getPos().getX(), vein.getPos().getY(), vein.getPos().getZ());
				}
			}
		}
	}
}
