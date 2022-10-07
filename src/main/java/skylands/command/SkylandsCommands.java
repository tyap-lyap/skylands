package skylands.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import nota.player.SongPlayer;
import skylands.data.Components;
import skylands.logic.Skylands;
import skylands.util.Texts;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.command.argument.BlockPosArgumentType.blockPos;
import static net.minecraft.command.argument.EntityArgumentType.player;

public class SkylandsCommands {

	public static void init() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> SkylandsCommands.register(dispatcher));
	}

	private static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("sl").then(literal("create").executes(context -> {
			var source = context.getSource();
			var player = source.getPlayer();
			if(player != null) {
				CreateCommand.run(player);
			}
			return 1;
		})));
		dispatcher.register(literal("sl").then(literal("hub").executes(context -> {
			var source = context.getSource();
			var player = source.getPlayer();
			MinecraftServer server = source.getServer();
			if(player != null) {
				HubCommands.visit(player, server);
			}
			return 1;
		})));
		dispatcher.register(literal("sl").then(literal("home").executes(context -> {
			var player = context.getSource().getPlayer();
			if(player != null) {
				HomeCommand.run(player);
			}
			return 1;
		})));
		dispatcher.register(literal("sl").then(literal("home").then(argument("player", word()).suggests((context, builder) -> {
			var player = context.getSource().getPlayer();

			if(player != null) {
				var islands = Components.PLAYER_DATA.get(player).getIslands();

				String remains = builder.getRemaining();

				for(String ownerName : islands) {
					if(ownerName.contains(remains)) {
						builder.suggest(ownerName);
					}
				}
				return builder.buildFuture();
			}
			return builder.buildFuture();
		}).executes(context -> {
			var ownerName = StringArgumentType.getString(context, "player");
			var visitor = context.getSource().getPlayer();
			if(visitor != null) {
				HomeCommand.run(visitor, ownerName);
			}
			return 1;
		}))));
		dispatcher.register(literal("sl").then(literal("visit").then(argument("player", player()).executes(context -> {
			var visitor = context.getSource().getPlayer();
			var owner = EntityArgumentType.getPlayer(context, "player");
			if(visitor != null && owner != null) {
				VisitCommand.run(visitor, owner);
			}
			return 1;
		}))));
		dispatcher.register(literal("sl").then(literal("members").then(literal("invite").then(argument("player", player()).executes(context -> {
			var player = context.getSource().getPlayer();
			var newcomer = EntityArgumentType.getPlayer(context, "player");
			if(player != null && newcomer != null) {
				MemberCommands.invite(player, newcomer);
			}
			return 1;
		})))));
		dispatcher.register(literal("sl").then(literal("members").then(literal("remove").then(argument("player", word()).suggests((context, builder) -> {
			var player = context.getSource().getPlayer();

			if(player != null) {
				var island = Skylands.instance.islands.get(player);
				if(island.isPresent()) {
					var members = island.get().members;

					String remains = builder.getRemaining();

					for(var member : members) {
						if(member.name.contains(remains)) {
							builder.suggest(member.name);
						}
					}
					return builder.buildFuture();
				}
			}
			return builder.buildFuture();
		}).executes(context -> {
			String memberToRemove = StringArgumentType.getString(context, "player");
			var player = context.getSource().getPlayer();
			if(player != null) {
				MemberCommands.remove(player, memberToRemove);
			}
			return 1;
		})))));
		dispatcher.register(literal("sl").then(literal("ban").then(argument("player", player()).executes(context -> {
			var player = context.getSource().getPlayer();
			var bannedPlayer = EntityArgumentType.getPlayer(context, "player");
			if(player != null && bannedPlayer != null) {
				BanCommands.ban(player, bannedPlayer);
			}
			return 1;
		}))));
		dispatcher.register(literal("sl").then(literal("kick").then(argument("player", player()).executes(context -> {
			var player = context.getSource().getPlayer();
			var kickedPlayer = EntityArgumentType.getPlayer(context, "player");
			if(player != null && kickedPlayer != null) {
				KickCommand.run(player, kickedPlayer);
			}
			return 1;
		}))));
		dispatcher.register(literal("sl").then(literal("help").executes(context -> {
			ServerPlayerEntity player = context.getSource().getPlayer();
			if(player != null) {
				HelpCommand.run(player);
			}
			return 1;
		})));

		dispatcher.register(literal("force-sl").requires(source -> source.hasPermissionLevel(4)).then(literal("set-hub-pos").then(argument("position", blockPos()).executes(context -> {
			var pos = BlockPosArgumentType.getBlockPos(context, "position");
			var source = context.getSource();
			HubCommands.setPos(pos, source);
			return 1;
		}))).then(literal("delete-island").then(CommandManager.argument("player", word()).executes(context -> {
			var playerName = StringArgumentType.getString(context, "player");
			var island = Skylands.instance.islands.get(playerName);

			if(island.isPresent()) {
				Skylands.instance.islands.delete(playerName);
				context.getSource().sendFeedback(Texts.of("message.skylands.force_delete.success", map -> map.put("%player%", playerName)), true);
			}
			else {
				context.getSource().sendFeedback(Texts.of("message.skylands.force_delete.fail", map -> map.put("%player%", playerName)), true);
			}

			return 1;
		}))).then(literal("toggle-hub-protection").executes(context -> {
			HubCommands.toggleProtection(context.getSource());
			return 1;
		})));

		dispatcher.register(literal("force-sl").requires(source -> source.hasPermissionLevel(4)).then(literal("toggle-hub-music").executes(context -> {
			MinecraftServer server = context.getSource().getServer();
			Skylands skylands = Skylands.instance;
			SongPlayer sp = skylands.hub.songPlayer;
			if(sp != null) {
				sp.setPlaying(!sp.playing);
			}
			else {
				skylands.hub.initSongPlayer(server);
				server.getPlayerManager().getPlayerList().forEach(player -> skylands.hub.songPlayer.addPlayer(player));
				skylands.hub.songPlayer.setPlaying(true);
			}
			return 1;
		})));

		dispatcher.register(literal("sl").then(literal("accept").then(argument("player", word()).executes(context -> {
			String inviter = StringArgumentType.getString(context, "player");
			var player = context.getSource().getPlayer();

			if(player != null) {
				AcceptCommand.run(player, inviter);
			}
			return 1;
		}))));

		dispatcher.register(literal("sl").then(literal("unban").then(argument("player", word()).suggests((context, builder) -> {
			var player = context.getSource().getPlayer();

			if(player != null) {
				var island = Skylands.instance.islands.get(player);
				if(island.isPresent()) {
					var bans = island.get().bans;

					String remains = builder.getRemaining();

					for(var member : bans) {
						if(member.name.contains(remains)) {
							builder.suggest(member.name);
						}
					}
					return builder.buildFuture();
				}
			}
			return builder.buildFuture();
		}).executes(context -> {
			String unbanned = StringArgumentType.getString(context, "player");
			var player = context.getSource().getPlayer();

			if(player != null) {
				BanCommands.unban(player, unbanned);
			}
			return 1;
		}))));

		dispatcher.register(literal("sl").then(literal("delete").executes(context -> {
			var player = context.getSource().getPlayer();

			if(player != null) {
				DeleteCommand.warn(player);
			}
			return 1;
		}).then(argument("confirmation", StringArgumentType.word()).executes(context -> {
			var player = context.getSource().getPlayer();
			String confirmWord = StringArgumentType.getString(context, "confirmation");

			if(player != null) {
				DeleteCommand.run(player, confirmWord);
			}
			return 1;
		}))));

//		dispatcher.register(literal("cutscene").then(argument("duration", IntegerArgumentType.integer()).executes(context -> {
//			var player = context.getSource().getPlayer();
//			var world = context.getSource().getWorld();
//			int duration = IntegerArgumentType.getInteger(context, "duration");
//
//			if(player != null) {
//				Cutscenes.create(world, player, duration);
//			}
//			return 1;
//		})));
	}
}
