package skylands.command;

import com.mojang.brigadier.CommandDispatcher;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import skylands.logic.Skylands;
import skylands.util.SkylandsTexts;

import static net.minecraft.command.argument.BlockPosArgumentType.blockPos;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SettingCommands {

	static void init(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("sl").then(literal("settings").then(literal("toggle-visits").requires(Permissions.require("skylands.settings.lock", true)).executes(context -> {
			var player = context.getSource().getPlayer();
			if(player != null) {
				SettingCommands.toggleVisits(player);
			}
			return 1;
		})).then(literal("set-spawn-pos").requires(Permissions.require("skylands.settings.spawn.position", true)).then(argument("position", blockPos()).executes(context -> {
			var player = context.getSource().getPlayer();
			var pos = BlockPosArgumentType.getBlockPos(context, "position");
			if(player != null) {
				SettingCommands.setSpawnPos(player, pos);
			}
			return 1;
		}))).then(literal("set-visits-pos").requires(Permissions.require("skylands.settings.visits.position", true)).then(argument("position", blockPos()).executes(context -> {
			var player = context.getSource().getPlayer();
			var pos = BlockPosArgumentType.getBlockPos(context, "position");
			if(player != null) {
				SettingCommands.setVisitsPos(player, pos);
			}
			return 1;
		})))));
	}

	static void toggleVisits(ServerPlayerEntity player) {
		Skylands.instance.islands.get(player).ifPresentOrElse(island -> {
			if(island.locked) {
				player.sendMessage(SkylandsTexts.prefixed("message.skylands.settings.unlock"));
				island.locked = false;
			}
			else {
				player.sendMessage(SkylandsTexts.prefixed("message.skylands.settings.lock"));
				island.locked = true;
			}

		}, () -> player.sendMessage(SkylandsTexts.prefixed("message.skylands.settings.no_island")));
	}

	static void setSpawnPos(ServerPlayerEntity player, BlockPos pos) {
		Skylands.instance.islands.get(player).ifPresentOrElse(island -> {
			island.spawnPos = new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
			String posText = pos.getX() + " " + pos.getY() + " " + pos.getZ();
			player.sendMessage(SkylandsTexts.prefixed("message.skylands.settings.spawn_pos_change", map -> map.put("%pos%", posText)));

		}, () -> player.sendMessage(SkylandsTexts.prefixed("message.skylands.settings.no_island")));
	}

	static void setVisitsPos(ServerPlayerEntity player, BlockPos pos) {
		Skylands.instance.islands.get(player).ifPresentOrElse(island -> {
			island.visitsPos = new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
			String posText = pos.getX() + " " + pos.getY() + " " + pos.getZ();
			player.sendMessage(SkylandsTexts.prefixed("message.skylands.settings.visits_pos_change", map -> map.put("%pos%", posText)));

		}, () -> player.sendMessage(SkylandsTexts.prefixed("message.skylands.settings.no_island")));
	}
}
