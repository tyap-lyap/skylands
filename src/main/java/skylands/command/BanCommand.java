package skylands.command;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import skylands.Mod;
import skylands.logic.Member;
import skylands.logic.Skylands;
import skylands.util.Texts;

import java.util.UUID;

public class BanCommand {

	static void run(ServerPlayerEntity player, ServerPlayerEntity banned) {
		Skylands.instance.islandStuck.get(player).ifPresentOrElse(island -> {
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

						if(banned.getWorld().getRegistryKey().getValue().getNamespace().equals(Mod.MOD_ID)) {
							var uuid = UUID.fromString(banned.getWorld().getRegistryKey().getValue().getPath());
							if(uuid.equals(island.owner.uuid)) {
								banned.sendMessage(Texts.prefixed("message.skylands.hub_visit"));
								FabricDimensions.teleport(banned, Skylands.instance.server.getOverworld(), new TeleportTarget(Skylands.instance.hub.pos, new Vec3d(0, 0, 0), 0, 0));
							}
						}
					}
				}
			}
		}, () -> player.sendMessage(Texts.prefixed("message.skylands.ban_player.no_island")));
	}
}
