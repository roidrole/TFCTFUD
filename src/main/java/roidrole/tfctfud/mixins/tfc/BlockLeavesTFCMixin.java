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
	@Unique private static IntList tfctfud_queueX1 = new IntArrayList();
	@Unique private static IntList tfctfud_queueY1 = new IntArrayList();
	@Unique private static IntList tfctfud_queueZ1 = new IntArrayList();
	@Unique private static IntList tfctfud_queueX2 = new IntArrayList();
	@Unique private static IntList tfctfud_queueY2 = new IntArrayList();
	@Unique private static IntList tfctfud_queueZ2 = new IntArrayList();

	@Unique
	private static final MutablerBlockPos tfctfud_decayPos = new MutablerBlockPos();

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

		tfctfud_queueX1.clear();
		tfctfud_queueY1.clear();
		tfctfud_queueZ1.clear();
		tfctfud_queueX2.clear();
		tfctfud_queueY2.clear();
		tfctfud_queueZ2.clear();

		tfctfud_queueX1.add(posIn.getX());
		tfctfud_queueY1.add(posIn.getY());
		tfctfud_queueZ1.add(posIn.getZ());
		for (int i = 1; i < radius; i++) {
			for (int j = 0; j < tfctfud_queueX1.size(); j++) {
				final int xOrigin = tfctfud_queueX1.getInt(j);
				final int yOrigin = tfctfud_queueY1.getInt(j);
				final int zOrigin = tfctfud_queueZ1.getInt(j);

				for(EnumFacing facing : EnumFacing.VALUES){
					final int x = xOrigin + facing.getXOffset();
					final int y = yOrigin + facing.getYOffset();
					final int z = zOrigin + facing.getZOffset();
					final int relPos = (x - minX) + (y - minY) * sideSize + (z - minZ) * sideSizeSq;
					if(evaluated[relPos]){
						continue;
					}
					tfctfud_decayPos.setPos(x, y, z);
					if(!world.isBlockLoaded(tfctfud_decayPos)){
						continue;
					}
					final IBlockState stateCheck = world.getBlockState(tfctfud_decayPos);
					if (stateCheck.getBlock() == log) {
						return;
					}
					if (stateCheck.getBlock() == this) {
						tfctfud_queueX2.add(x);
						tfctfud_queueY2.add(y);
						tfctfud_queueZ2.add(z);
					}
					evaluated[relPos] = true;
				}
			}
			tfctfud_queueX1.clear();
			tfctfud_queueY1.clear();
			tfctfud_queueZ1.clear();
			//Swap queue 1 and 2. Since all 1 queue are equal (but must remain distinct), we can make this easier
			final IntList tempQueue = tfctfud_queueZ1;
			tfctfud_queueZ1 = tfctfud_queueZ2;
			tfctfud_queueZ2 = tfctfud_queueY1;
			tfctfud_queueY1 = tfctfud_queueY2;
			tfctfud_queueY2 = tfctfud_queueX1;
			tfctfud_queueX1 = tfctfud_queueX2;
			tfctfud_queueX2 = tempQueue;
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
