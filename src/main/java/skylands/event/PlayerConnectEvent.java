package skylands.event;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerConnectEvent {

	public static void onJoin(MinecraftServer server, ServerPlayerEntity player) {
//		Skylands.instance.islandStuck.get(player).ifPresent(Island::getWorld);
	}

	public static void onLeave(MinecraftServer server, ServerPlayerEntity player) {

	}
}
