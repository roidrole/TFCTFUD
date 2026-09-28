package roidrole.tfctfud.mixins.tfc;

import net.dries007.tfc.api.util.FallingBlockManager;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import roidrole.tfctfud.utils.BlockStateMap;

import java.util.Map;

@Mixin(FallingBlockManager.class)
public abstract class FallingBlockManagerMixin {
	@Mutable
	@Shadow(remap = false)
	@Final
	private static Map<IBlockState, FallingBlockManager.Specification> FALLABLES;

	@Inject(
		method = "<clinit>",
		at = @At(
			value = "TAIL"
		)
	)
	private static void useBetterMap(CallbackInfo ci){
		FALLABLES = new BlockStateMap<>();
	}

	/**
	 * @author roidrole
	 * @reason Use fast path if registering a Block
	 */
	@Overwrite(remap = false)
	public static void registerFallable(Block block, FallingBlockManager.Specification specification)
	{
		((BlockStateMap<FallingBlockManager.Specification>)FALLABLES).put(block, specification);
	}
}
