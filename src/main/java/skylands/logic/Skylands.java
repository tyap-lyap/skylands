package skylands.logic;

import net.minecraft.server.MinecraftServer;
import xyz.nucleoid.fantasy.Fantasy;

public class Skylands {
	public static Skylands instance;
	public MinecraftServer server;
	public Fantasy fantasy;
	public IslandStuck islandStuck;
	public SkylandsHub hub;

	public Skylands(MinecraftServer server) {
		this.server = server;
		this.fantasy = Fantasy.get(server);
		this.islandStuck = new IslandStuck();
		this.hub = new SkylandsHub();
	}

	public static Skylands getInstance() {
		return Skylands.instance;
	}

	public void onTick(MinecraftServer server) {

	}

}
