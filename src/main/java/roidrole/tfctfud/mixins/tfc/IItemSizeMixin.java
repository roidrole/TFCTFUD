package roidrole.tfctfud.mixins.tfc;

import net.dries007.tfc.api.capability.size.IItemSize;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import javax.annotation.Nonnull;
import java.util.List;

@Mixin(IItemSize.class)
public interface IItemSizeMixin extends IItemSize{
	/**
	 * @author roidrole
	 * @reason Item size info use a translation key
	 */
	@SideOnly(Side.CLIENT)
	@Overwrite(remap = false)
	default void addSizeInfo(@Nonnull ItemStack stack, @Nonnull List<String> text){
		text.add(
			 "⚖ " + I18n.format("tfc.capability.weight."+getWeight(stack).name) +
			" ⇲ " + I18n.format("tfc.capability.size."+getSize(stack).name)
		);
	}
}
