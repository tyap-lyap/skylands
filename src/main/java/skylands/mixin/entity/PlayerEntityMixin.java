package skylands.mixin.entity;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import skylands.Mod;
import skylands.logic.Island;
import skylands.logic.Skylands;

import java.util.UUID;

@Mixin(ServerPlayerEntity.class)
public abstract class PlayerEntityMixin extends PlayerEntity {

	public PlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile, @Nullable PlayerPublicKey publicKey) {
		super(world, pos, yaw, gameProfile, publicKey);
	}

	@Override
	protected void tickPortal() {
		if(world.getRegistryKey().getValue().getNamespace().equals(Mod.MOD_ID)) {
			var uuid = UUID.fromString(world.getRegistryKey().getValue().getPath());
			var island = Skylands.instance.islands.get(uuid);
			island.ifPresent(this::tickPortalOnIsland);
		}
		else if(world.getRegistryKey().getValue().getNamespace().equals("nether")) {
			var uuid = UUID.fromString(world.getRegistryKey().getValue().getPath());
			var island = Skylands.instance.islands.get(uuid);
			island.ifPresent(this::tickPortalInNether);
		}
		else {
			super.tickPortal();
		}
	}

	protected void tickPortalOnIsland(Island island) {
		if (this.world instanceof ServerWorld serverWorld) {
			int i = this.getMaxNetherPortalTime();
			if (this.inNetherPortal) {
				MinecraftServer server = serverWorld.getServer();
				ServerWorld nether = island.getNether();

				if (nether != null && server.isNetherAllowed() && !this.hasVehicle() && this.netherPortalTime++ >= i) {
					this.world.getProfiler().push("portal");
					this.netherPortalTime = i;
					this.resetPortalCooldown();
					FabricDimensions.teleport(this, nether, new TeleportTarget(new Vec3d(0.5D, 75D, 0.5D), new Vec3d(0, 0, 0), 0, 0));
					this.world.getProfiler().pop();
				}

				this.inNetherPortal = false;
			}
			else {
				if (this.netherPortalTime > 0) {
					this.netherPortalTime -= 4;
				}

				if (this.netherPortalTime < 0) {
					this.netherPortalTime = 0;
				}
			}

			this.tickPortalCooldown();
		}
	}

	protected void tickPortalInNether(Island island) {
		if (this.world instanceof ServerWorld serverWorld) {
			int i = this.getMaxNetherPortalTime();
			if (this.inNetherPortal) {
				MinecraftServer server = serverWorld.getServer();
				ServerWorld islandWorld = island.getWorld();

				if (islandWorld != null && server.isNetherAllowed() && !this.hasVehicle() && this.netherPortalTime++ >= i) {
					this.world.getProfiler().push("portal");
					this.netherPortalTime = i;
					this.resetPortalCooldown();
					FabricDimensions.teleport(this, islandWorld, new TeleportTarget(island.spawnPos, new Vec3d(0, 0, 0), 0, 0));
					this.world.getProfiler().pop();
				}

				this.inNetherPortal = false;
			}
			else {
				if (this.netherPortalTime > 0) {
					this.netherPortalTime -= 4;
				}

				if (this.netherPortalTime < 0) {
					this.netherPortalTime = 0;
				}
			}

			this.tickPortalCooldown();
		}
	}
}
