package skylands.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import skylands.logic.Skylands;

public class ServerTickEvent implements ServerTickEvents.EndTick {
	static final ServerTickEvent INSTANCE = new ServerTickEvent();

	@Override
	public void onEndTick(MinecraftServer server) {
		Skylands.getInstance().onTick(server);
	}
}
