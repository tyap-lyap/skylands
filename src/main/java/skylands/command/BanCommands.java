package skylands.command;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import skylands.logic.Member;
import skylands.logic.Skylands;
import skylands.util.Texts;
import skylands.util.Worlds;

public class BanCommands {

	static void ban(ServerPlayerEntity player, ServerPlayerEntity banned) {
		Skylands.instance.islands.get(player).ifPresentOrElse(island -> {
			if(player.getName().getString().equals(banned.getName().getString())) {
				player.sendMessage(Texts.prefixed("message.skylands.ban_player.yourself"));
			}
			else {
				if(island.isMember(banned)) {
					player.sendMessage(Texts.prefixed("message.skylands.ban_player.member"));
				}
				else {
					if(island.isBanned(banned)) {
						player.sendMessage(Texts.prefixed("message.skylands.ban_player.fail"));
					}
					else {
						island.bans.add(new Member(banned));
						player.sendMessage(Texts.prefixed("message.skylands.ban_player.success", map -> map.put("%player%", banned.getName().getString())));
						banned.sendMessage(Texts.prefixed("message.skylands.ban_player.ban", map -> map.put("%owner%", island.owner.name)));

						Worlds.getIsland(banned.getWorld()).ifPresent(isl -> {
							if(isl.owner.uuid.equals(island.owner.uuid)) {
								banned.sendMessage(Texts.prefixed("message.skylands.hub_visit"));
								FabricDimensions.teleport(banned, Skylands.instance.server.getOverworld(), new TeleportTarget(Skylands.instance.hub.pos, new Vec3d(0, 0, 0), 0, 0));
							}
						});
					}
				}
			}
		}, () -> player.sendMessage(Texts.prefixed("message.skylands.ban_player.no_island")));
	}

	static void unban(ServerPlayerEntity player, String unbanned) {
		Skylands.instance.islands.get(player).ifPresentOrElse(island -> {
			if(!island.isBanned(unbanned)) {
				player.sendMessage(Texts.prefixed("message.skylands.unban_player.fail"));
			}
			else {
				island.bans.removeIf(member -> member.name.equals(unbanned));
				player.sendMessage(Texts.prefixed("message.skylands.unban_player.success", map -> map.put("%player%", unbanned)));
			}
		}, () -> player.sendMessage(Texts.prefixed("message.skylands.unban_player.no_island")));
	}
}
