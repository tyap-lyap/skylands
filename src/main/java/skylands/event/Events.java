package skylands.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.util.TypedActionResult;

public class Events {

	public static void init() {
		ServerLifecycleEvents.SERVER_STARTING.register(ServerStartEvent::onStart);
		ServerTickEvents.END_SERVER_TICK.register(ServerTickEvent::onTick);
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> PlayerConnectEvent.onJoin(server, handler.player));
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> PlayerConnectEvent.onLeave(server, handler.player));
		PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
			if(!world.isClient) {
				return BlockBreakEvent.onBreak(world, player, pos, state);
			}
			return true;
		});
		UseItemCallback.EVENT.register((player, world, hand) -> {
			if(!world.isClient) {
				return UseItemEvent.onUse(player, world, hand);
			}
			return TypedActionResult.pass(player.getStackInHand(hand));
		});
	}
}
