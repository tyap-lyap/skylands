package skylands.command;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import skylands.Mod;
import skylands.logic.Skylands;

public class Visit {

	// TODO: visitors should have different spawn point and
	// TODO: it should be configurable by an island's owner
	static void cmd(ServerPlayerEntity visitor, ServerPlayerEntity owner) {
		String ownerName = owner.getName().getString();

		Skylands.instance.islandStuck.get(owner).ifPresentOrElse(island -> {
			if(!island.isMember(visitor) && island.isBanned(visitor)) {
				visitor.sendMessage(Text.of("Skylands > You are banned from visiting " + ownerName + "'s Island!"));
			}
			else {
				if(visitor.getWorld().getRegistryKey().getValue().equals(Mod.id(island.owner.uuid.toString()))) {
					visitor.sendMessage(Text.of("Skylands > You are already on the " + ownerName + "'s Island!"));
				}
				else {
					visitor.sendMessage(Text.of("Skylands > Teleporting to the " + ownerName + "'s Island!"));
					island.visit(visitor);
				}
			}

		}, () -> visitor.sendMessage(Text.of("Skylands > " + ownerName + " doesn't have an Island yet!")));
	}
}
