package skylands.command;

import net.minecraft.server.network.ServerPlayerEntity;
import skylands.logic.Skylands;
import skylands.util.Players;
import skylands.util.Texts;

public class AcceptCommand {

	static void run(ServerPlayerEntity player, String ownerName) {
		var inviter = Players.get(ownerName);
		if(inviter.isPresent()) {
			var island = Skylands.instance.islandStuck.get(inviter.get());
			if(island.isPresent()) {
				var invite = Skylands.instance.invites.get(island.get(), player);
				if(invite.isPresent()) {
					if(!invite.get().accepted) {
						invite.get().accept(player);
						player.sendMessage(Texts.prefixed("message.skylands.accept.success", map -> map.put("%owner%", ownerName)));
					}
				}
				else {
					player.sendMessage(Texts.prefixed("message.skylands.accept.fail"));
				}
			}
			else {
				player.sendMessage(Texts.prefixed("message.skylands.accept.no_island"));
			}
		}
		else {
			player.sendMessage(Texts.prefixed("message.skylands.accept.no_player"));
		}
	}
}
