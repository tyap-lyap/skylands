package skylands.event;

import net.minecraft.server.MinecraftServer;
import skylands.logic.Skylands;

public class ServerStartEvent {

	public static void onStart(MinecraftServer server) {
		Skylands.instance = new Skylands(server);
	}
}
