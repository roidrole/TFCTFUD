package roidrole.tfctfud.mixins.tfc;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.sugar.Local;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.te.TEPlacedItemFlat;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.classic.worldgen.WorldGenLooseRocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = WorldGenLooseRocks.class, remap = false)
public abstract class WorldGenLooseRocksMixin {
	//TFC's tileEntity is only used to perform a single null check at the start
	//We want to only place the TE if the stack is not null
	//As such, we redirect the Helpers.getTE call to return a dummy TE whose only feature is not being null.
	@Unique
	private static final TEPlacedItemFlat tfctfud_dummyTile = new TEPlacedItemFlat();

	@Redirect(
		method = "generateRock",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z"
		)
	)
	private boolean noUnconditionalBlockstatePlacing(World instance, BlockPos pos, IBlockState newState, int flags){
		//No-op
		return false;
	}

	@Redirect(
		method = "generateRock",
		at = @At(
			value = "INVOKE",
			target = "Lnet/dries007/tfc/util/Helpers;getTE(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;Ljava/lang/Class;)Lnet/minecraft/tileentity/TileEntity;"
		)
	)
	private TileEntity getDummyTile(IBlockAccess world, BlockPos pos, Class<?> aClass){
		return tfctfud_dummyTile;
	}

	@ModifyReceiver(
		method = "generateRock",
		at = @At(
			value = "INVOKE",
			target = "Lnet/dries007/tfc/objects/te/TEPlacedItemFlat;setStack(Lnet/minecraft/item/ItemStack;)V"
		)
	)
	private TEPlacedItemFlat createTEIfStackNotNull(
		TEPlacedItemFlat instance,
		ItemStack stack,
		@Local(argsOnly = true) World world,
		@Local(argsOnly = true) BlockPos pos
	){
		world.setBlockState(pos, BlocksTFC.PLACED_ITEM_FLAT.getDefaultState(), 2);
		return Helpers.getTE(world, pos, TEPlacedItemFlat.class);
	}
}
