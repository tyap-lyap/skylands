package skylands.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import skylands.logic.Island;
import skylands.logic.IslandStuck;
import skylands.logic.Skylands;
import skylands.util.SkylandsTexts;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CreateCommand {

	static void init(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("sl").then(literal("create").requires(Permissions.require("skylands.create", true)).executes(context -> {
			var source = context.getSource();
			var player = source.getPlayer();
			if(player != null) {
				CreateCommand.run(player);
			}
			return 1;
		}).then(argument("template", StringArgumentType.greedyString()).suggests((context, builder) -> {
			var player = context.getSource().getPlayer();

			if(player != null) {
				String remains = builder.getRemaining();

				for(var template : Skylands.config.islandTemplates) {
					if(template.name.contains(remains)) {
						if(template.permission == null || Permissions.check(player, template.permission)) {
							builder.suggest(template.name);
						}
					}
				}
				return builder.buildFuture();
			}
			return builder.buildFuture();
		}).executes(context -> {
			var source = context.getSource();
			var player = source.getPlayer();
			var template = StringArgumentType.getString(context, "template");

			if(player != null) {
				CreateCommand.run(player, template);
			}
			return 1;
		}))));
	}

	static void run(ServerPlayerEntity player) {
		IslandStuck islands = Skylands.instance.islands;

		if(islands.get(player).isPresent()) {
			player.sendMessage(SkylandsTexts.prefixed("message.skylands.island_create.fail"));
		}
		else {
			Island island = islands.create(player);
			if(Skylands.config.teleportAfterIslandCreation) {
				island.visitAsMember(player);
			}
			player.sendMessage(SkylandsTexts.prefixed("message.skylands.island_create.success"));
		}
	}

	static void run(ServerPlayerEntity player, String templateName) {
		IslandStuck islands = Skylands.instance.islands;

		if(islands.get(player).isPresent()) {
			player.sendMessage(SkylandsTexts.prefixed("message.skylands.island_create.fail"));
		}
		else {
			var template = Skylands.config.getIslandTemplate(templateName);
			Island island;

			if(template.isPresent() && (template.get().permission == null || Permissions.check(player, template.get().permission))) {
				island = islands.create(player, templateName);
			}
			else {
				island = islands.create(player);
			}

			if(Skylands.config.teleportAfterIslandCreation) {
				island.visitAsMember(player);
			}
			player.sendMessage(SkylandsTexts.prefixed("message.skylands.island_create.success"));
		}
	}
}
