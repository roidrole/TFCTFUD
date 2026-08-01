package roidrole.tfctfud.mixins.tfc;

import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.world.classic.worldgen.vein.IVeinExpansion;
import net.dries007.tfc.world.classic.worldgen.vein.Vein;
import net.dries007.tfc.world.classic.worldgen.vein.VeinType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import roidrole.tfctfud.utils.MutablerBlockPos;

import java.util.Random;

@Mixin(Vein.class)
public abstract class VeinMixin implements IVeinExpansion {
	@Shadow(remap = false)
	@Final
	protected BlockPos pos;

	@Shadow(remap = false)
	@Final
	protected VeinType type;

	@Shadow(remap = false)
	public abstract int getLowestY();

	@Shadow(remap = false)
	public abstract int getHighestY();

	@Shadow(remap = false)
	public abstract double getChanceToGenerate(BlockPos pos);

	@Shadow(remap = false)
	public abstract boolean canSpawnIn(Rock rock);

	@Shadow(remap = false)
	public abstract IBlockState getOreState(Rock rock);

	/**
	 * @author roidrole
	 * @reason Avoid computing y distance that is always 0
	 */
	@Overwrite(remap = false)
	public boolean inRange(int x, int z, int extraDistance)
	{
		int dx = pos.getX() - x;
		int dz = pos.getZ() - z;
		return dx * dx + dz * dz < (type.getWidth() + extraDistance) * (type.getWidth() + extraDistance);
	}

	@Override
	public boolean tfctfud_generate(World world, BlockPos chunkBlockPos, Random random){
		boolean generated = false;
		MutablerBlockPos posAt = new MutablerBlockPos();
		for (int x = chunkBlockPos.getX() + 8; x < chunkBlockPos.getX() + 24; x++)
		{
			for (int z = chunkBlockPos.getZ() + 8; z < chunkBlockPos.getZ() + 24; z++)
			{
				// Do checks here that are specific to the horizontal position, not the vertical one
				if (this.inRange(x, z, 0))
				{
					for (int y = this.getLowestY(); y <= this.getHighestY(); y++)
					{
						posAt.setPos(x, y, z);
						final IBlockState stateAt = world.getBlockState(posAt);

						// Do checks specific to the individual block pos that is getting replaced
						if (stateAt.getBlock() instanceof BlockRockVariant && random.nextDouble() < this.getChanceToGenerate(posAt))
						{
							final BlockRockVariant blockAt = (BlockRockVariant) stateAt.getBlock();
							if (blockAt.getType() == Rock.Type.RAW && this.canSpawnIn(blockAt.getRock()))
							{
								world.setBlockState(posAt, this.getOreState(blockAt.getRock()), 2);
								generated = true;
							}
						}
					}
				}
			}
		}
		return generated;
	}
}
