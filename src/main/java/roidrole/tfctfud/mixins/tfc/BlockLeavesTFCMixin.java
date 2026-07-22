package roidrole.tfctfud.mixins.tfc;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.objects.blocks.wood.BlockLeavesTFC;
import net.dries007.tfc.objects.blocks.wood.BlockLogTFC;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import roidrole.tfctfud.utils.MutablerBlockPos;

import static net.dries007.tfc.Constants.RNG;

@Mixin(BlockLeavesTFC.class)
public abstract class BlockLeavesTFCMixin extends BlockLeaves {
	@Shadow(remap = false)
	@Final
	public Tree wood;

	//Queues of blockpos to visit, split and static to avoid excessive allocation
	@Unique private static IntList tfutils_queueX1 = new IntArrayList();
	@Unique private static IntList tfutils_queueY1 = new IntArrayList();
	@Unique private static IntList tfutils_queueZ1 = new IntArrayList();
	@Unique private static IntList tfutils_queueX2 = new IntArrayList();
	@Unique private static IntList tfutils_queueY2 = new IntArrayList();
	@Unique private static IntList tfutils_queueZ2 = new IntArrayList();

	@Unique
	private static final MutablerBlockPos tfutils_decayPos = new MutablerBlockPos();

	/**
	 * @author roidrole
	 * @reason Use a boolean[] instead of a HashSet for efficiency, lower memory allocation and temp object creation
	 */
	@Overwrite(remap = false)
	private void doLeafDecay(World world, BlockPos posIn, IBlockState state)
	{
		if (world.isRemote || !state.getValue(DECAYABLE)){
			return;
		}
		final int radius = wood.getMaxDecayDistance();
		final int sideSize = 2 * radius + 1;
		final int sideSizeSq = sideSize * sideSize;

		//The evaluated cache. Allocated every call. The JVM will happily optimize this
		final boolean[] evaluated = new boolean[sideSizeSq * sideSize];
		int minX = posIn.getX() - radius;
		int minY = posIn.getY() - radius;
		int minZ = posIn.getZ() - radius;

		final BlockLogTFC log = BlockLogTFC.get(wood);

		tfutils_queueX1.clear();
		tfutils_queueY1.clear();
		tfutils_queueZ1.clear();
		tfutils_queueX2.clear();
		tfutils_queueY2.clear();
		tfutils_queueZ2.clear();

		tfutils_queueX1.add(posIn.getX());
		tfutils_queueY1.add(posIn.getY());
		tfutils_queueZ1.add(posIn.getZ());
		for (int i = 1; i < radius; i++) {
			for (int j = 0; j < tfutils_queueX1.size(); j++) {
				final int xOrigin = tfutils_queueX1.getInt(j);
				final int yOrigin = tfutils_queueY1.getInt(j);
				final int zOrigin = tfutils_queueZ1.getInt(j);

				for(EnumFacing facing : EnumFacing.VALUES){
					final int x = xOrigin + facing.getXOffset();
					final int y = yOrigin + facing.getYOffset();
					final int z = zOrigin + facing.getZOffset();
					final int relPos = (x - minX) + (y - minY) * sideSize + (z - minZ) * sideSizeSq;
					if(evaluated[relPos]){
						continue;
					}
					tfutils_decayPos.setPos(x, y, z);
					if(!world.isBlockLoaded(tfutils_decayPos)){
						continue;
					}
					final IBlockState stateCheck = world.getBlockState(tfutils_decayPos);
					if (stateCheck.getBlock() == log) {
						return;
					}
					if (stateCheck.getBlock() == this) {
						tfutils_queueX2.add(x);
						tfutils_queueY2.add(y);
						tfutils_queueZ2.add(z);
					}
					evaluated[relPos] = true;
				}
			}
			tfutils_queueX1.clear();
			tfutils_queueY1.clear();
			tfutils_queueZ1.clear();
			//Swap queue 1 and 2. Since all 1 queue are equal (but must remain distinct), we can make this easier
			final IntList tempQueue = tfutils_queueZ1;
			tfutils_queueZ1 = tfutils_queueZ2;
			tfutils_queueZ2 = tfutils_queueY1;
			tfutils_queueY1 = tfutils_queueY2;
			tfutils_queueY2 = tfutils_queueX1;
			tfutils_queueX1 = tfutils_queueX2;
			tfutils_queueX2 = tempQueue;
		}

		world.setBlockToAir(posIn);
		final int particleScale = 10;
		final double x = posIn.getX();
		final double y = posIn.getY();
		final double z = posIn.getZ();
		for (int i = 1; i < RNG.nextInt(4); i++)
		{
			switch (RNG.nextInt(4))
			{
				case 1:
					TFCParticles.LEAF1.sendToAllNear(world, x + RNG.nextFloat() / particleScale, y - RNG.nextFloat() / particleScale, z + RNG.nextFloat() / particleScale, (RNG.nextFloat() - 0.5) / particleScale, -0.15D + RNG.nextFloat() / particleScale, (RNG.nextFloat() - 0.5) / particleScale, 90);
					break;
				case 2:
					TFCParticles.LEAF2.sendToAllNear(world, x + RNG.nextFloat() / particleScale, y - RNG.nextFloat() / particleScale, z + RNG.nextFloat() / particleScale, (RNG.nextFloat() - 0.5) / particleScale, -0.15D + RNG.nextFloat() / particleScale, (RNG.nextFloat() - 0.5) / particleScale, 70);
					break;
				case 3:
					TFCParticles.LEAF3.sendToAllNear(world, x + RNG.nextFloat() / particleScale, y - RNG.nextFloat() / particleScale, z + RNG.nextFloat() / particleScale, (RNG.nextFloat() - 0.5) / particleScale, -0.15D + RNG.nextFloat() / particleScale, (RNG.nextFloat() - 0.5) / particleScale, 80);
					break;
			}
		}
	}
}
