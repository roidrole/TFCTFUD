package net.dries007.tfc.world.classic.worldgen.vein;

import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import roidrole.tfctfud.utils.MutablerBlockPos;

import java.util.Random;

public class TFCTFUDVeinCluster extends Vein implements IVeinExpansion {
	private final TFCTFUDVeinCluster.Cluster[] spawnPoints;

	public TFCTFUDVeinCluster(BlockPos pos, VeinType veinType, Ore.Grade grade, Random rand)
	{
		super(pos, veinType, grade);

		// Individual vein width is 60% - 100% of type width (it must fit exactly inside the circle described by width)
		double maxWidth = (0.6 + rand.nextDouble() * 0.4) * veinType.getWidth();
		double maxHeight = (0.6 + rand.nextDouble() * 0.4) * veinType.getHeight();
		double maxClusterSize = 0.6 * maxWidth;

		int clusterCount = 4 + rand.nextInt(5);
		spawnPoints = new TFCTFUDVeinCluster.Cluster[clusterCount];

		int radius = (int) Math.ceil(maxClusterSize * (0.6 + 0.4 * rand.nextDouble()));
		spawnPoints[0] = new TFCTFUDVeinCluster.Cluster(pos, radius);
		for (int i = 1; i < clusterCount; i++)
		{
			final BlockPos clusterPos = pos.add(
				maxWidth * 0.4 * rand.nextDouble(),
				maxHeight * 0.4 * rand.nextDouble(),
				maxWidth * 0.4 * rand.nextDouble()
			);
			radius = (int) Math.ceil(maxClusterSize * (0.4 + 0.6 * rand.nextDouble()));
			spawnPoints[i] = new TFCTFUDVeinCluster.Cluster(clusterPos, radius);
		}
	}


	@Override
	public boolean tfctfud_generate(World world, BlockPos chunkBlockPos, Random random) {
		MutablerBlockPos mutablerBlockPos = new MutablerBlockPos();
		boolean generated = false;
		final int minx = chunkBlockPos.getX() + 8;
		final int maxx = minx + 16;
		final int miny = this.getLowestY();
		final int maxy = this.getHighestY();
		final int minz = chunkBlockPos.getZ() + 8;
		final int maxz = minz + 16;

		for (Cluster cluster: spawnPoints){
			int mindx = Math.max(-cluster.radius, minx - cluster.x);
			int mindy = Math.max(-cluster.radius, miny - cluster.y);
			int mindz = Math.max(-cluster.radius, minz - cluster.z);
			int maxdx = Math.min(cluster.radius, maxx - cluster.x);
			int maxdy = Math.min(cluster.radius, maxy - cluster.y);
			int maxdz = Math.min(cluster.radius, maxz - cluster.z);
			mutablerBlockPos.setPos(cluster.x, cluster.y, cluster.z);
			for (int dx = mindx; dx < maxdx; dx++) {
				int dx2 = dx * dx;
				for (int dy = mindy; dy < maxdy; dy++) {
					int dy2 = dy * dy;
					for (int dz = mindz; dz < maxdz; dz++) {
						double distance = cluster.radiusInvSq * (dx2 + dy2 + dz * dz);
						double chance = type.getDensity() * Math.min(5*(1 - distance), distance);
						if(random.nextDouble() < chance){
							mutablerBlockPos.translate(dx, dy, dz);
							if(attemptGeneration(world, mutablerBlockPos)){
								generated = true;
							}
							mutablerBlockPos.translate(-dx, -dy, -dz);
						}
					}
				}
			}
		}

		return generated;
	}

	private boolean attemptGeneration(World world, BlockPos posAt){
		final IBlockState stateAt = world.getBlockState(posAt);

		// Do checks specific to the individual block pos that is getting replaced
		if (stateAt.getBlock() instanceof BlockRockVariant)
		{
			final BlockRockVariant blockAt = (BlockRockVariant) stateAt.getBlock();
			if (blockAt.getType() == Rock.Type.RAW && this.canSpawnIn(blockAt.getRock()))
			{
				world.setBlockState(posAt, this.getOreState(blockAt.getRock()), 2);
				return true;
			}
		}
		return false;
	}

	private static final class Cluster
	{
		final int x;
		final int y;
		final int z;
		final int radius;
		final double radiusInvSq;

		Cluster(BlockPos pos, int radius)
		{
			this.x = pos.getX();
			this.y = pos.getY();
			this.z = pos.getZ();
			this.radius = radius;
			this.radiusInvSq = 1.0d/(radius * radius);
		}
	}
}
