package roidrole.tfctfud.mixins.tfc;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.dries007.tfc.world.classic.spawner.WorldEntitySpawnerTFC;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;
import java.util.Map;

@Mixin(WorldEntitySpawnerTFC.class)
public abstract class WorldEntitySpawnerTFCMixin {
	@Unique
	private static final Map<EntityEntry, Entity> tfctfud_entityCache = new HashMap<>();

	@WrapOperation(
		method = "lambda$performWorldGenSpawning$18",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraftforge/fml/common/registry/EntityEntry;newInstance(Lnet/minecraft/world/World;)Lnet/minecraft/entity/Entity;"
		),
		remap = false
	)
	private static Entity useEntityCache(EntityEntry instance, World world, Operation<Entity> original){
		return tfctfud_entityCache.computeIfAbsent(instance, entry -> original.call(instance, world));
	}
}
