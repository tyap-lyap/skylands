package skylands.mixin.entity;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import skylands.logic.Skylands;
import skylands.util.TeleportUtil;
import skylands.util.WorldProtection;
import skylands.util.Worlds;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {


	public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile, @Nullable PlayerPublicKey publicKey) {
		super(world, pos, yaw, gameProfile, publicKey);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	void tick(CallbackInfo ci) {
		ServerPlayerEntity player = ServerPlayerEntity.class.cast(this);

		if(!WorldProtection.canModify(world, player)) {
			if(player.getPos().getY() < world.getDimension().minY() - 10) {
				Worlds.getIsland(world).ifPresentOrElse(island -> {
					var pos = island.spawnPos;
					TeleportUtil.teleport(player, island.getWorld(), pos.getX(), pos.getY(), pos.getZ(), 0, 0);
				}, () -> {
					Skylands.instance.hub.visit(player);
				});
			}
		}
	}

	@Redirect(method = "moveToWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getRegistryKey()Lnet/minecraft/util/registry/RegistryKey;"))
	public RegistryKey<World> moveToWorld_redirectRegistryKey(ServerWorld instance) {
		return Worlds.redirect(instance.getRegistryKey());
	}

	@Redirect(method = "getTeleportTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getRegistryKey()Lnet/minecraft/util/registry/RegistryKey;"))
	public RegistryKey<World> getTeleportTarget_redirectRegistryKey(ServerWorld instance) {
		return Worlds.redirect(instance.getRegistryKey());
	}

	@Redirect(method = "worldChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getRegistryKey()Lnet/minecraft/util/registry/RegistryKey;"))
	public RegistryKey<World> worldChanged_redirectRegistryKey(ServerWorld instance) {
		return Worlds.redirect(instance.getRegistryKey());
	}

}
