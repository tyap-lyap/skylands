package skylands.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import skylands.config.SkylandsConfigCommands;
import skylands.logic.Skylands;
import skylands.util.Texts;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

import static com.mojang.brigadier.arguments.StringArgumentType.word;

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

		dispatcher.register(literal("force-sl").then(literal("delete-island").requires(Permissions.require("skylands.force.delete", 4)).then(argument("player", word()).executes(context -> {
			var playerName = StringArgumentType.getString(context, "player");
			var island = Skylands.instance.islands.get(playerName);

			if(island.isPresent()) {
				Skylands.instance.islands.delete(playerName);
				context.getSource().sendFeedback(Texts.prefixed("message.skylands.force_delete.success", map -> map.put("%player%", playerName)), true);
			}
			else {
				context.getSource().sendFeedback(Texts.prefixed("message.skylands.force_delete.fail", map -> map.put("%player%", playerName)), true);
			}

			return 1;
		}))));
		SkylandsConfigCommands.init(dispatcher);
	}
}
