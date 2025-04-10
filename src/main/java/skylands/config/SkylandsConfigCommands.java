package skylands.config;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import skylands.logic.Skylands;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.command.argument.EntityArgumentType.player;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.command.argument.BlockPosArgumentType.blockPos;

public class SkylandsConfigCommands {

	public static void init(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("skylands-admin").then(literal("config").requires(Permissions.require("skylands.admin.config", 4)).then(literal("island-deletion-cooldown").then(argument("cooldown", integer()).executes(context -> {
			var cooldown = IntegerArgumentType.getInteger(context, "cooldown");
			Skylands.config.islandDeletionCooldown = cooldown;
			Skylands.config.save();
			context.getSource().sendFeedback(() -> Text.of("config.islandDeletionCooldown has changed to: " + cooldown), true);
			return 1;

		}))).then(literal("teleport-after-island-creation").executes(context -> {
			var config = Skylands.config;
			config.teleportAfterIslandCreation = !config.teleportAfterIslandCreation;
			config.save();
			context.getSource().sendFeedback(() -> Text.of("config.teleportAfterIslandCreation has changed to: " + config.teleportAfterIslandCreation), true);
			return 1;

		})).then(literal("hub-protected-by-default").executes(context -> {
			var config = Skylands.config;
			config.hubProtectedByDefault = !config.hubProtectedByDefault;
			config.save();
			context.getSource().sendFeedback(() -> Text.of("config.hubProtectedByDefault has changed to: " + config.hubProtectedByDefault), true);
			return 1;

		})).then(literal("create-island-on-player-join").executes(context -> {
			var config = Skylands.config;
			config.createIslandOnPlayerJoin = !config.createIslandOnPlayerJoin;
			config.save();
			context.getSource().sendFeedback(() -> Text.of("config.createIslandOnPlayerJoin has changed to: " + config.createIslandOnPlayerJoin), true);
			return 1;

		})).then(literal("toggle-update-checker").executes(context -> {
			var config = Skylands.config;
			config.updateCheckerEnabled = !config.updateCheckerEnabled;
			config.save();
			context.getSource().sendFeedback(() -> Text.of("config.updateCheckerEnabled has changed to: " + config.updateCheckerEnabled), true);
			return 1;

		})).then(literal("toggle-end-dimension-islands").executes(context -> {
			var config = Skylands.config;
			config.endDimensionIslandsEnabled = !config.endDimensionIslandsEnabled;
			config.save();
			context.getSource().sendFeedback(() -> Text.of("config.endDimensionIslandsEnabled has changed to: " + config.endDimensionIslandsEnabled), true);
			return 1;
		})).then(literal("reload").executes(context -> {
			Skylands.config = SkylandsConfig.read();
			context.getSource().sendFeedback(() -> Text.of("Config successfully reloaded!"), true);
			return 1;
		})).then(literal("reset").executes(context -> {
			Skylands.config = new SkylandsConfig();
			Skylands.config.save();
			context.getSource().sendFeedback(() -> Text.of("Config was successfully reset to default!"), true);
			return 1;
		})).then(literal("root-command").then(argument("root-command", word()).executes(context -> {
			var config = Skylands.config;
			config.rootCommand = StringArgumentType.getString(context, "root-command");
			config.save();
			context.getSource().sendFeedback(() -> Text.of("Root command was changed to: " + config.rootCommand + ", server restart is required!"), true);
			return 1;
		})))));

	}
}
