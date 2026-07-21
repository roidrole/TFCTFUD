package net.dries007.tfc.world.classic.worldgen.vein;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public interface IVeinExpansion {
	boolean tfutils_generate(World world, BlockPos chunkBlockPos, Random random);
}
