package skylands.event;

import net.minecraft.server.MinecraftServer;
import skylands.logic.Cutscenes;
import skylands.logic.Skylands;

public class ServerTickEvent {

	public static void onTick(MinecraftServer server) {
		Skylands.getInstance().onTick(server);
		Cutscenes.onTick(server);
	}
}
