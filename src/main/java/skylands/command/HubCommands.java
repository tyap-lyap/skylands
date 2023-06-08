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

public class HubCommands {

	static void init(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("sl").then(literal("hub").requires(Permissions.require("skylands.hub", true)).executes(context -> {
			var source = context.getSource();
			var player = source.getPlayer();

			if(player != null) {
				HubCommands.visit(player);
			}
			return 1;
		})));

		dispatcher.register(literal("force-sl").then(literal("hub").then(literal("set-spawn-pos").requires(Permissions.require("skylands.force.hub.position", 4)).then(argument("position", blockPos()).executes(context -> {
			var pos = BlockPosArgumentType.getBlockPos(context, "position");
			var source = context.getSource();
			HubCommands.setPos(pos, source);
			return 1;
		}))).then(literal("toggle-protection").requires(Permissions.require("skylands.force.hub.protection", 4)).executes(context -> {
			HubCommands.toggleProtection(context.getSource());
			return 1;
		}))));
	}

	public static void visit(ServerPlayerEntity player) {
		player.sendMessage(SkylandsTexts.prefixed("message.skylands.hub_visit"));
		Skylands.getHub().visit(player);
	}

	static void setPos(BlockPos pos, ServerCommandSource source) {
		Skylands.getHub().pos = new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
		String posText = pos.getX() + " " + pos.getY() + " " + pos.getZ();
		source.sendFeedback(() -> SkylandsTexts.prefixed("message.skylands.hub_pos_change", map -> map.put("%pos%", posText)), true);
	}

	static void toggleProtection(ServerCommandSource source) {
		var hub = Skylands.getHub();
		if(hub.hasProtection) {
			hub.hasProtection = false;
			source.sendFeedback(() -> SkylandsTexts.prefixed("message.skylands.hub_protection.disable"), true);
		}
		else {
			hub.hasProtection = true;
			source.sendFeedback(() -> SkylandsTexts.prefixed("message.skylands.hub_protection.enable"), true);
		}
	}
}
