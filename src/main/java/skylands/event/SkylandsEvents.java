package skylands.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import nota.Nota;
import nota.event.SongStartEvent;
import nota.player.PositionSongPlayer;
import skylands.util.Texts;
import skylands.util.WorldProtection;

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
		ServerLifecycleEvents.SERVER_STARTING.register(ServerStartEvent::onStarting);
		ServerLifecycleEvents.SERVER_STARTED.register(ServerStartEvent::onStarted);
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
				if(!WorldProtection.canModify(world, player)) {
					player.sendMessage(Texts.prefixed("message.skylands.world_protection.harvest"), true);
					return ActionResult.PASS;
				}

				BlockPos pos = hitResult.getBlockPos();
				BlockState state = world.getBlockState(pos);
				ItemStack toolStack = player.getStackInHand(hand);

				if(state.getBlock() instanceof CropBlock crop && crop.isMature(state)) {
					Item replant = state.getBlock().getPickStack(world, pos, state).getItem();
					final boolean[] removedReplant = {false};

					Block.getDroppedStacks(state, (ServerWorld)world, pos, null, player, toolStack).forEach(stack -> {
						if (!removedReplant[0] && stack.getItem() == replant) {
							stack.setCount(stack.getCount() - 1);
							removedReplant[0] = true;
						}

						Block.dropStack(world, pos, stack);
					});

					state.onStacksDropped((ServerWorld)world, pos, toolStack, true);
					world.setBlockState(pos, crop.withAge(0));

					return ActionResult.SUCCESS;
				}

			}
			return ActionResult.PASS;
		});
	}
}
