package skylands.mixin.entity;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import skylands.logic.Island;
import skylands.util.Worlds;

import java.util.Optional;

@Mixin(Entity.class)
public abstract class EntityMixin {

	@Shadow
	public World world;

	@ModifyVariable(method = "tickPortal", at = @At("STORE"), ordinal = 0)
	public RegistryKey<World> tickPortal_modifyRegistryKey(RegistryKey<World> instance) {
		if (Worlds.isIsland(world) && Worlds.isOverworld(world.getRegistryKey())) {
			Optional<Island> island = Worlds.getIsland(world);
			if (island.isPresent()) {
				return island.get().getNether().getRegistryKey();
			}
		}
		if (Worlds.isIsland(world) && Worlds.isNether(world.getRegistryKey())) {
			Optional<Island> island = Worlds.getIsland(world);
			if (island.isPresent()) {
				return island.get().getWorld().getRegistryKey();
			}
		}
		return instance;
	}

	@Redirect(method = "getTeleportTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getRegistryKey()Lnet/minecraft/util/registry/RegistryKey;"))
	public RegistryKey<World> getTeleportTarget_redirectRegistryKey0(ServerWorld instance) {
		return Worlds.redirect(instance.getRegistryKey());
	}

	@Redirect(method = "getTeleportTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getRegistryKey()Lnet/minecraft/util/registry/RegistryKey;"))
	public RegistryKey<World> getTeleportTarget_redirectRegistryKey(World instance) {
		return Worlds.redirect(instance.getRegistryKey());
	}


	@Redirect(method = "moveToWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getRegistryKey()Lnet/minecraft/util/registry/RegistryKey;", ordinal = 0))
	public RegistryKey<World> moveToWorld_redirectRegistryKey(ServerWorld instance) {
		return Worlds.redirect(instance.getRegistryKey());
	}

}
