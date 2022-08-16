package skylands.command;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import skylands.logic.Skylands;
import skylands.util.Players;

public class Accept {

	static void cmd(ServerPlayerEntity player, String ownerName) {
		var inviter = Players.get(ownerName);
		if(inviter.isPresent()) {
			var island = Skylands.instance.islandStuck.get(inviter.get());
			if(island.isPresent()) {
				var invite = Skylands.instance.invites.get(island.get(), player);
				if(invite.isPresent()) {
					if(!invite.get().accepted) {
						invite.get().accept(player);
						player.sendMessage(Text.of("Skylands > You successfully accepted " + ownerName + "'s invite! You can now visit the island with \"/sl home " + ownerName + "\" command."));
					}
				}
				else {
					player.sendMessage(Text.of("Skylands > This player did not invite you."));
				}
			}
			else {
				player.sendMessage(Text.of("Skylands > This player does not have an island yet."));
			}
		}
		else {
			player.sendMessage(Text.of("Skylands > Such player does not exist!"));
		}
	}
}
