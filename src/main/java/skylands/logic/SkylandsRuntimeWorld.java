package skylands.logic;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import xyz.nucleoid.fantasy.RuntimeWorld;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;

public class SkylandsRuntimeWorld extends RuntimeWorld {

	protected SkylandsRuntimeWorld(MinecraftServer server, RegistryKey<World> registryKey, RuntimeWorldConfig config, Style style) {
		super(server, registryKey, config, style);
	}

	@Override
	public GameRules getGameRules() {
		return getServer().getGameRules();
	}
}
