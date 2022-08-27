package skylands.command;

import net.minecraft.server.network.ServerPlayerEntity;
import skylands.Mod;
import skylands.logic.Skylands;
import skylands.util.Texts;

public class HomeCommand {

	static void run(ServerPlayerEntity player) {
		Skylands.instance.islandStuck.get(player).ifPresentOrElse(island -> {
			if(player.getWorld().getRegistryKey().getValue().equals(Mod.id(player.getUuid().toString()))) {
				player.sendMessage(Texts.prefixed("message.skylands.home.fail"));
			}
			else {
				player.sendMessage(Texts.prefixed("message.skylands.home.success"));
				island.visit(player);
			}
		}, () -> player.sendMessage(Texts.prefixed("message.skylands.home.no_island")));
	}

	static void run(ServerPlayerEntity visitor, String islandOwner) {
		Skylands.instance.islandStuck.get(islandOwner).ifPresentOrElse(island -> {
			if(visitor.getWorld().getRegistryKey().getValue().equals(Mod.id(island.owner.uuid.toString()))) {
				visitor.sendMessage(Texts.prefixed("message.skylands.visit_home.fail", map -> map.put("%owner%", islandOwner)));
			}
			else {
				if(island.isMember(visitor)) {
					visitor.sendMessage(Texts.prefixed("message.skylands.visit_home.success", map -> map.put("%owner%", islandOwner)));
					island.visit(visitor);
				}
				else {
					visitor.sendMessage(Texts.prefixed("message.skylands.visit_home.not_member"));
				}
			}
		}, () -> visitor.sendMessage(Texts.prefixed("message.skylands.visit_home.no_island")));
	}
}
