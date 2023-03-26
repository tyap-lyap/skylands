package skylands.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
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

		ServerLifecycleEvents.SERVER_STARTING.register(ServerStartEvent::onStarting);
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
		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if(!world.isClient) {
				return UseEntityEvent.onUse(player, world, hand, entity);
			}
			return ActionResult.PASS;
		});

		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if(!world.isClient) {
				return UseBlockEvent.onBlockUse(player, world, hand, hitResult);
			}
			return ActionResult.PASS;
		});
	}
}
