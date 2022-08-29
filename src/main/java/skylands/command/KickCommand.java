package skylands.command;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import skylands.logic.Skylands;
import skylands.util.Texts;
import skylands.util.Worlds;

public class KickCommand {

	static void run(ServerPlayerEntity player, ServerPlayerEntity kicked) {
		Skylands.instance.islandStuck.get(player).ifPresentOrElse(island -> {
			if(player.getName().getString().equals(kicked.getName().getString())) {
				player.sendMessage(Texts.prefixed("message.skylands.kick_visitor.yourself"));
			}
			else {
				if(island.isMember(kicked)) {
					player.sendMessage(Texts.prefixed("message.skylands.kick_visitor.member"));
				}
				else {
					Worlds.getIsland(kicked.getWorld()).ifPresent(isl -> {
						if(isl.owner.uuid.equals(island.owner.uuid)) {
							player.sendMessage(Texts.prefixed("message.skylands.kick_visitor.success", map -> map.put("%player%", kicked.getName().getString())));

							kicked.sendMessage(Texts.prefixed("message.skylands.kick_visitor.kick", map -> map.put("%owner%", player.getName().getString())));
							kicked.sendMessage(Texts.prefixed("message.skylands.hub_visit"));
							FabricDimensions.teleport(kicked, Skylands.instance.server.getOverworld(), new TeleportTarget(Skylands.instance.hub.pos, new Vec3d(0, 0, 0), 0, 0));
						}
						else {
							player.sendMessage(Texts.prefixed("message.skylands.kick_visitor.fail", map -> map.put("%player%", kicked.getName().getString())));
						}
					});
				}
			}
		}, () -> player.sendMessage(Texts.prefixed("message.skylands.kick_visitor.no_island")));
	}
}
