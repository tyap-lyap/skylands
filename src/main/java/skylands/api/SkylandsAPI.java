package skylands.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import skylands.logic.Island;
import skylands.logic.Skylands;
import skylands.util.Worlds;

import java.util.Optional;
import java.util.UUID;

public class SkylandsAPI {
	static Skylands skylands = Skylands.getInstance();

	public static Optional<Island> getIsland(PlayerEntity player) {
		return skylands.islands.get(player);
	}

	public static Optional<Island> getIsland(String playerName) {
		return skylands.islands.get(playerName);
	}

	public static Optional<Island> getIsland(UUID playerUuid) {
		return skylands.islands.get(playerUuid);
	}

	public static Optional<Island> getIsland(World world) {
		return Worlds.getIsland(world);
	}

}
