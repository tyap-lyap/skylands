package skylands.command;

import net.minecraft.server.network.ServerPlayerEntity;
import skylands.Mod;
import skylands.logic.Skylands;
import skylands.util.Texts;

public class VisitCommand {

	// TODO: visitors should have different spawn point and
	// TODO: it should be configurable by an island's owner
	static void run(ServerPlayerEntity visitor, ServerPlayerEntity owner) {
		String ownerName = owner.getName().getString();

		Skylands.instance.islands.get(owner).ifPresentOrElse(island -> {
			if(!island.isMember(visitor) && island.isBanned(visitor)) {
				visitor.sendMessage(Texts.prefixed("message.skylands.island_visit.ban", map -> map.put("%owner%", ownerName)));
			}
			else {
				if(visitor.getWorld().getRegistryKey().getValue().equals(Mod.id(island.owner.uuid.toString()))) {
					visitor.sendMessage(Texts.prefixed("message.skylands.island_visit.fail", map -> map.put("%owner%", ownerName)));
				}
				else {
					visitor.sendMessage(Texts.prefixed("message.skylands.island_visit.success", map -> map.put("%owner%", ownerName)));
					island.visit(visitor);
				}
			}

		}, () -> visitor.sendMessage(Texts.prefixed("message.skylands.island_visit.no_island", map -> map.put("%owner%", ownerName))));
	}
}
