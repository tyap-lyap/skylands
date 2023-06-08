package skylands.config;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.BlockPosArgumentType;
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
		dispatcher.register(literal("force-sl").then(literal("config").requires(Permissions.require("skylands.force.config", 4)).then(literal("default-spawn-pos").then(argument("position", blockPos()).executes(context -> {
			var pos = BlockPosArgumentType.getBlockPos(context, "position");
			Skylands.config.defaultSpawnPos = new PlayerPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
			Skylands.config.save();
			String posText = pos.getX() + " " + pos.getY() + " " + pos.getZ();
			context.getSource().sendFeedback(() -> Text.of("config.defaultSpawnPos has changed to: " + posText), true);
			return 1;

		}))).then(literal("default-visits-pos").then(argument("position", blockPos()).executes(context -> {
			var pos = BlockPosArgumentType.getBlockPos(context, "position");
			Skylands.config.defaultVisitsPos = new PlayerPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
			Skylands.config.save();
			String posText = pos.getX() + " " + pos.getY() + " " + pos.getZ();
			context.getSource().sendFeedback(() -> Text.of("config.defaultVisitsPos has changed to: " + posText), true);
			return 1;

		}))).then(literal("default-hub-pos").then(argument("position", blockPos()).executes(context -> {
			var pos = BlockPosArgumentType.getBlockPos(context, "position");
			Skylands.config.defaultHubPos = new PlayerPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
			Skylands.config.save();
			String posText = pos.getX() + " " + pos.getY() + " " + pos.getZ();
			context.getSource().sendFeedback(() -> Text.of("config.defaultHubPos has changed to: " + posText), true);
			return 1;

		}))).then(literal("island-deletion-cooldown").then(argument("cooldown", integer()).executes(context -> {
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

		})).then(literal("toggle-right-click-harvest").executes(context -> {
			var config = Skylands.config;
			config.rightClickHarvestEnabled = !config.rightClickHarvestEnabled;
			config.save();
			context.getSource().sendFeedback(() -> Text.of("config.rightClickHarvestEnabled has changed to: " + config.rightClickHarvestEnabled), true);
			return 1;
		})).then(literal("reload").executes(context -> {
			Skylands.config = SkylandsConfig.read();
			context.getSource().sendFeedback(() -> Text.of("Config successfully reloaded!"), true);
			return 1;
		}))));

	}
}
