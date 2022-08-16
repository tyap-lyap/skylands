package skylands.command;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import skylands.Mod;
import skylands.logic.Skylands;

public class Home {

	static void cmd(ServerPlayerEntity player) {
		Skylands.instance.islandStuck.get(player).ifPresentOrElse(island -> {
			if(player.getWorld().getRegistryKey().getValue().equals(Mod.id(player.getUuid().toString()))) {
				player.sendMessage(Text.of("Skylands > You are already on your island!"));
			}
			else {
				player.sendMessage(Text.of("Skylands > Teleporting to the Island!"));
				island.visit(player);
			}
		}, () -> player.sendMessage(Text.of("Skylands > You don't have an island yet!")));
	}

	static void cmd(ServerPlayerEntity visitor, String islandOwner) {
		Skylands.instance.islandStuck.get(islandOwner).ifPresentOrElse(island -> {
			if(visitor.getWorld().getRegistryKey().getValue().equals(Mod.id(island.owner.uuid.toString()))) {
				visitor.sendMessage(Text.of("Skylands > You are already on the " + islandOwner + "'s Island!"));
			}
			else {
				if(island.isMember(visitor)) {
					visitor.sendMessage(Text.of("Skylands > Teleporting to the " + islandOwner + "'s Island!"));
					island.visit(visitor);
				}
				else {
					visitor.sendMessage(Text.of("Skylands > You are not member of this island."));
				}
			}
		}, () -> visitor.sendMessage(Text.of("Skylands > This player doesn't have an island yet!")));
	}
}
