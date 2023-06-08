package skylands.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import skylands.config.SkylandsConfigCommands;
import skylands.logic.Skylands;
import skylands.util.SkylandsTexts;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class SkylandsCommands {
	public static final SuggestionProvider<ServerCommandSource> SUGGEST_ISLANDS = (context, builder) -> {
		String remains = builder.getRemaining();

		for(var island : Skylands.getIslands().stuck) {
			if(island.owner.name.contains(remains)) {
				builder.suggest(island.owner.name);
			}
		}
		return builder.buildFuture();
	};

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

		dispatcher.register(literal("force-sl").then(literal("delete").requires(Permissions.require("skylands.force.delete", 4)).then(argument("player", word()).suggests(SUGGEST_ISLANDS).executes(context -> {
			var playerName = StringArgumentType.getString(context, "player");

			Skylands.getIslands().get(playerName).ifPresentOrElse(island -> {
				Skylands.instance.islands.delete(playerName);
				SkylandsTexts.prefixed(context, "message.skylands.force.delete.success", map -> map.put("%player%", playerName));
			}, () -> {
				SkylandsTexts.prefixed(context, "message.skylands.force.delete.fail", map -> map.put("%player%", playerName));
			});

			return 1;
		}))));

		dispatcher.register(literal("force-sl").then(literal("visit").requires(Permissions.require("skylands.force.visit", 4)).then(argument("player", word()).suggests(SUGGEST_ISLANDS).executes(context -> {
			var playerName = StringArgumentType.getString(context, "player");
			var admin = context.getSource().getPlayer();
			if(admin != null) {
				Skylands.getIslands().get(playerName).ifPresentOrElse(island -> {
					island.visitAsMember(admin);
					SkylandsTexts.prefixed(context, "message.skylands.force.visit.success", map -> map.put("%player%", playerName));
				}, () -> {
					SkylandsTexts.prefixed(context, "message.skylands.force.visit.fail", map -> map.put("%player%", playerName));
				});
			}
			return 1;
		}))));
		SkylandsConfigCommands.init(dispatcher);
	}
}
