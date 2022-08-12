package skylands.event;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import skylands.logic.Skylands;

@SuppressWarnings("unused")
public class PlayerConnectEvent {

	public static void onJoin(MinecraftServer server, ServerPlayerEntity player) {
		Skylands.instance.islandStuck.get(player).ifPresent(island -> {
			island.owner.name = player.getName().getString();
		});
	}

	public static void onLeave(MinecraftServer server, ServerPlayerEntity player) {

	}
}
