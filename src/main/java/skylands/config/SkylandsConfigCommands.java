package skylands.config;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import skylands.logic.Skylands;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.command.argument.EntityArgumentType.player;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.command.argument.BlockPosArgumentType.blockPos;

public class SkylandsConfigCommands {

	public static void init(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("force-sl").requires(source -> source.hasPermissionLevel(4)).then(literal("config").then(literal("default-spawn-pos").then(argument("position", blockPos()).executes(context -> {
			var pos = BlockPosArgumentType.getBlockPos(context, "position");
			Skylands.instance.config.defaultSpawnPos = new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
			Skylands.instance.config.save();
			String posText = pos.getX() + " " + pos.getY() + " " + pos.getZ();
			context.getSource().sendFeedback(Text.of("config.defaultSpawnPos has changed to: " + posText), true);
			return 1;

		}))).then(literal("default-visits-pos").then(argument("position", blockPos()).executes(context -> {
			var pos = BlockPosArgumentType.getBlockPos(context, "position");
			Skylands.instance.config.defaultVisitsPos = new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
			Skylands.instance.config.save();
			String posText = pos.getX() + " " + pos.getY() + " " + pos.getZ();
			context.getSource().sendFeedback(Text.of("config.defaultVisitsPos has changed to: " + posText), true);
			return 1;

		}))).then(literal("teleport-after-island-creation").executes(context -> {
			var config = Skylands.instance.config;
			config.teleportAfterIslandCreation = !config.teleportAfterIslandCreation;
			config.save();
			context.getSource().sendFeedback(Text.of("config.teleportAfterIslandCreation has changed to: " + config.teleportAfterIslandCreation), true);
			return 1;

		})).then(literal("create-island-on-player-join").executes(context -> {
			var config = Skylands.instance.config;
			config.createIslandOnPlayerJoin = !config.createIslandOnPlayerJoin;
			config.save();
			context.getSource().sendFeedback(Text.of("config.createIslandOnPlayerJoin has changed to: " + config.createIslandOnPlayerJoin), true);
			return 1;

		})).then(literal("toggle-update-checker").executes(context -> {
			var config = Skylands.instance.config;
			config.updateCheckerEnabled = !config.updateCheckerEnabled;
			config.save();
			context.getSource().sendFeedback(Text.of("config.updateCheckerEnabled has changed to: " + config.updateCheckerEnabled), true);
			return 1;

		})).then(literal("toggle-right-click-harvest").executes(context -> {
			var config = Skylands.instance.config;
			config.rightClickHarvestEnabled = !config.rightClickHarvestEnabled;
			config.save();
			context.getSource().sendFeedback(Text.of("config.rightClickHarvestEnabled has changed to: " + config.rightClickHarvestEnabled), true);
			return 1;
		})).then(literal("reload").executes(context -> {
			Skylands.instance.config = SkylandsConfig.read();
			context.getSource().sendFeedback(Text.of("Config successfully reloaded!"), true);
			return 1;
		}))));

	}
}
