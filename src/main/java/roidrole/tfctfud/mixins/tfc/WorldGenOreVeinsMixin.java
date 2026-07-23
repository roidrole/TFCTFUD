package roidrole.tfctfud.mixins.tfc;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.world.classic.ChunkGenTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;
import net.dries007.tfc.world.classic.worldgen.WorldGenOreVeins;
import net.dries007.tfc.world.classic.worldgen.vein.IVeinExpansion;
import net.dries007.tfc.world.classic.worldgen.vein.Vein;
import net.dries007.tfc.world.classic.worldgen.vein.VeinRegistry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Random;

import static net.dries007.tfc.world.classic.worldgen.WorldGenOreVeins.CHUNK_RADIUS;
import static net.dries007.tfc.world.classic.worldgen.WorldGenOreVeins.getNearbyVeins;

@Mixin(WorldGenOreVeins.class)
public abstract class WorldGenOreVeinsMixin {
	@Shadow(remap = false)
	@Final
	private static Random LOCAL_RANDOM;

	/**
	 * @author roidrole
	 * @reason Avoid list creation
	 */
	@Overwrite(remap = false)
	// Gets veins at a single chunk. Deterministic for a specific chunk x/z and world seed
	private static void getVeinsAtChunk(List<Vein> listToAdd, int chunkX, int chunkZ, long worldSeed)
	{
		LOCAL_RANDOM.setSeed(worldSeed + chunkX * 341873128712L + chunkZ * 132897987541L);
		VeinRegistry.INSTANCE.getVeins().values().stream()
			.filter(veinType -> LOCAL_RANDOM.nextInt(veinType.getRarity()) == 0)
			.map(veinType -> veinType.createVein(LOCAL_RANDOM, chunkX, chunkZ))
			.forEach(listToAdd::add)
		;
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
