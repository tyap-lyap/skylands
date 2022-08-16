package skylands.command;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import skylands.Mod;
import skylands.logic.Member;
import skylands.logic.Skylands;

import java.util.UUID;

public class Ban {

	static void cmd(ServerPlayerEntity player, ServerPlayerEntity banned) {
		Skylands.instance.islandStuck.get(player).ifPresentOrElse(island -> {
			if(player.getName().getString().equals(banned.getName().getString())) {
				player.sendMessage(Text.of("Skylands > You can't ban yourself."));
			}
			else {
				if(island.isMember(banned)) {
					player.sendMessage(Text.of("Skylands > You can't ban member of your island."));
				}
				else {
					if(island.isBanned(banned)) {
						player.sendMessage(Text.of("Skylands > This player is already banned."));
					}
					else {
						island.bans.add(new Member(banned));
						player.sendMessage(Text.of("Skylands > " + banned + " got successfully banned."));
						banned.sendMessage(Text.of("Skylands > You have been banned from visiting " + island.owner.name + "'s Island!"));

						if(banned.getWorld().getRegistryKey().getValue().getNamespace().equals(Mod.MOD_ID)) {
							var uuid = UUID.fromString(banned.getWorld().getRegistryKey().getValue().getPath());
							if(uuid.equals(island.owner.uuid)) {
								banned.sendMessage(Text.of("Skylands > Teleporting to the Hub!"));
								FabricDimensions.teleport(banned, Skylands.instance.server.getOverworld(), new TeleportTarget(Skylands.instance.hub.pos, new Vec3d(0, 0, 0), 0, 0));
							}
						}
					}
				}
			}
		}, () -> player.sendMessage(Text.of("Skylands > You don't have an island yet!")));
	}
}
