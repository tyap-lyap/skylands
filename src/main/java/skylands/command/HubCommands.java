package skylands.command;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import skylands.logic.Skylands;
import skylands.util.Texts;

public class HubCommands {

	static void visit(ServerPlayerEntity player, MinecraftServer server) {
		player.sendMessage(Texts.prefixed("message.skylands.hub_visit"));
		FabricDimensions.teleport(player, server.getOverworld(), new TeleportTarget(Skylands.instance.hub.pos, new Vec3d(0, 0, 0), 0, 0));
	}

	static void setPos(BlockPos pos, ServerCommandSource source) {
		Skylands.instance.hub.pos = new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
		String posText = pos.getX() + " " + pos.getY() + " " + pos.getZ();
		source.sendFeedback(Texts.prefixed("message.skylands.hub_pos_change", map -> map.put("%pos%", posText)), true);
	}

	static void toggleProtection(ServerCommandSource source) {
		var hub = Skylands.instance.hub;
		if(hub.hasProtection) {
			hub.hasProtection = false;
			source.sendFeedback(Texts.prefixed("message.skylands.hub_protection.disable"), true);
		}
		else {
			hub.hasProtection = true;
			source.sendFeedback(Texts.prefixed("message.skylands.hub_protection.enable"), true);
		}
	}
}
