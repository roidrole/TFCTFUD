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
	//Format: [x, y, z, x, y, z, ...]
	@Unique
	private static final IntList tfctfud_queue = new IntArrayList();

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
		final int minX = posIn.getX() - radius;
		final int minY = posIn.getY() - radius;
		final int minZ = posIn.getZ() - radius;

		final BlockLogTFC log = BlockLogTFC.get(wood);
		tfctfud_queue.clear();

		tfctfud_queue.add(posIn.getX());
		tfctfud_queue.add(posIn.getY());
		tfctfud_queue.add(posIn.getZ());
		int j = 0;
		for (int i = 1; i < radius; i++) {
			//Final index for this iteration.
			//We allow the list to grow relatively unbounded across iterations.
			//Since the list is static and every iteration grows O(n^2), this shouldn't waste too much RAM
			final int maxJ = tfctfud_queue.size();
			while (j < maxJ) {
				final int xOrigin = tfctfud_queue.getInt(j++);
				final int yOrigin = tfctfud_queue.getInt(j++);
				final int zOrigin = tfctfud_queue.getInt(j++);

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
						tfctfud_queue.add(x);
						tfctfud_queue.add(y);
						tfctfud_queue.add(z);
					}
					evaluated[relPos] = true;
				}
			}
		}

		//If the loop didn't return, it means that no log block was found
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
