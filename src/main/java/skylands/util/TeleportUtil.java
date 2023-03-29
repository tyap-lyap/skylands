package skylands.util;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.biome.source.BiomeAccess;

/**
 * Backport of 1.19.4's ServerPlayerEntity#teleport method
 */
public class TeleportUtil {

	public static boolean teleport(ServerPlayerEntity player, ServerWorld world, double destX, double destY, double destZ) {
		return teleport(player, world, destX, destY, destZ, 0, 0);
	}

	public static boolean teleport(ServerPlayerEntity player, ServerWorld world, double destX, double destY, double destZ, float yaw, float pitch) {
		ChunkPos chunkPos = new ChunkPos(new BlockPos(destX, destY, destZ));
		world.getChunkManager().addTicket(ChunkTicketType.POST_TELEPORT, chunkPos, 1, player.getId());
		player.stopRiding();
		if (player.isSleeping()) {
			player.wakeUp(true, true);
		}

		if (world == player.world) {
			FabricDimensions.teleport(player, world, new TeleportTarget(new Vec3d(destX, destY, destZ), Vec3d.ZERO, yaw, pitch));
		} else {
			teleportB(player, world, destX, destY, destZ, yaw, pitch);
		}

		player.setHeadYaw(yaw);
		return true;
	}

	static void teleportB(ServerPlayerEntity player, ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch) {
		player.setCameraEntity(player);
		player.stopRiding();
		if (targetWorld == player.world) {
			player.networkHandler.requestTeleport(x, y, z, yaw, pitch);
		} else {
			ServerWorld serverWorld = player.getWorld();
			WorldProperties worldProperties = targetWorld.getLevelProperties();
			player.networkHandler
					.sendPacket(
							new PlayerRespawnS2CPacket(
									targetWorld.getDimensionKey(),
									targetWorld.getRegistryKey(),
									BiomeAccess.hashSeed(targetWorld.getSeed()),
									player.interactionManager.getGameMode(),
									player.interactionManager.getPreviousGameMode(),
									targetWorld.isDebugWorld(),
									targetWorld.isFlat(),
									true,
									player.getLastDeathPos()
							)
					);
			player.networkHandler.sendPacket(new DifficultyS2CPacket(worldProperties.getDifficulty(), worldProperties.isDifficultyLocked()));
			player.server.getPlayerManager().sendCommandTree(player);
			serverWorld.removePlayer(player, Entity.RemovalReason.CHANGED_DIMENSION);
			player.unsetRemoved();
			player.refreshPositionAndAngles(x, y, z, yaw, pitch);
			player.setWorld(targetWorld);
			targetWorld.onPlayerTeleport(player);
			player.worldChanged(serverWorld);
			player.networkHandler.requestTeleport(x, y, z, yaw, pitch);
			player.server.getPlayerManager().sendWorldInfo(player, targetWorld);
			player.server.getPlayerManager().sendPlayerStatus(player);
		}
	}
}
