package skylands.world;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import skylands.logic.Island;
import xyz.nucleoid.fantasy.RuntimeWorld;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;

public class SkylandsRuntimeWorld extends RuntimeWorld {
	public final Island island;

	public SkylandsRuntimeWorld(Island island, MinecraftServer server, RegistryKey<World> registryKey, RuntimeWorldConfig config, Style style) {
		super(server, registryKey, config, style);
		this.island = island;
	}

	@Override
	public GameRules getGameRules() {
		return getServer().getGameRules();
	}
}
