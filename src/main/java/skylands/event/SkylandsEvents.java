package skylands.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import nota.Nota;
import nota.event.SongStartEvent;
import nota.player.PositionSongPlayer;
import skylands.util.Texts;

import java.util.UUID;

public class SkylandsEvents {

	public static void init() {
		SongStartEvent.EVENT.register(songPlayer -> {
			if(songPlayer.getId().equals(new Identifier("skylands:hub_song_player")) && songPlayer instanceof PositionSongPlayer sp) {
				for(UUID uuid : sp.getPlayerUUIDs()) {
					PlayerEntity player = Nota.getAPI().getServer().getPlayerManager().getPlayer(uuid);
					if(player != null && sp.isInRange(player)) {
						player.sendMessage(Texts.of("message.skylands.now_playing", map -> map.put("%song%", sp.getSong().getTitle())), true);
					}
				}
			}
		});
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
		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if(!world.isClient) {
				return UseEntityEvent.onUse(player, world, hand, entity);
			}
			return ActionResult.PASS;
		});
	}
}
