package skylands.command;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import skylands.Mod;
import skylands.logic.Skylands;

import java.util.UUID;

public class Kick {

	static void cmd(ServerPlayerEntity player, ServerPlayerEntity kicked) {
		Skylands.instance.islandStuck.get(player).ifPresentOrElse(island -> {
			if(player.getName().getString().equals(kicked.getName().getString())) {
				player.sendMessage(Text.of("Skylands > You can't kick yourself."));
			}
			else {
				if(island.isMember(kicked)) {
					player.sendMessage(Text.of("Skylands > You can't kick member of your island."));
				}
				else {
					if(kicked.getWorld().getRegistryKey().getValue().getNamespace().equals(Mod.MOD_ID)) {
						var uuid = UUID.fromString(kicked.getWorld().getRegistryKey().getValue().getPath());
						if(uuid.equals(island.owner.uuid)) {
							kicked.sendMessage(Text.of("Skylands > " + player.getName().getString() + " kicked you from their island."));
							kicked.sendMessage(Text.of("Skylands > Teleporting to the Hub!"));
							FabricDimensions.teleport(kicked, Skylands.instance.server.getOverworld(), new TeleportTarget(Skylands.instance.hub.pos, new Vec3d(0, 0, 0), 0, 0));
						}
						else {
							player.sendMessage(Text.of("Skylands > " + kicked.getName().getString() + " is not on your island."));
						}
					}
				}
			}
		}, () -> player.sendMessage(Text.of("Skylands > You don't have an island yet!")));
	}
}
