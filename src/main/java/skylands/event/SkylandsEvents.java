package skylands.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.world.World;
import skylands.logic.Skylands;

public class SkylandsEvents {

	public static void init() {
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, player, alive) -> {
			var server = player.getServer();
			if(server != null && server.getFile("hub_template").exists()) {
				if(player.getWorld().getRegistryKey().equals(World.OVERWORLD)) {
					Skylands.getInstance().hub.visit(player);
				}
			}
		});

		ServerLifecycleEvents.SERVER_STARTING.register(ServerStartEvent.INSTANCE);
		ServerTickEvents.END_SERVER_TICK.register(ServerTickEvent.INSTANCE);
		ServerPlayConnectionEvents.JOIN.register(PlayerConnectEvent.INSTANCE);
		ServerPlayConnectionEvents.DISCONNECT.register(PlayerConnectEvent.INSTANCE);
		PlayerBlockBreakEvents.BEFORE.register(BlockBreakEvent.INSTANCE);
	}
}
