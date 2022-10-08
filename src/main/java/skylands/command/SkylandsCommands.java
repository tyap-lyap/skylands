package skylands.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import nota.player.SongPlayer;
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
		CreateCommand.init(dispatcher);
		HubCommands.init(dispatcher);
		HomeCommand.init(dispatcher);
		VisitCommand.init(dispatcher);
		MemberCommands.init(dispatcher);
		BanCommands.init(dispatcher);
		KickCommand.init(dispatcher);
		HelpCommand.init(dispatcher);
		AcceptCommand.init(dispatcher);
		DeleteCommand.init(dispatcher);
		SettingCommands.init(dispatcher);

		dispatcher.register(literal("force-sl").requires(source -> source.hasPermissionLevel(4)).then(literal("delete-island").then(argument("player", word()).executes(context -> {
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
		}))));

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
	}
}
