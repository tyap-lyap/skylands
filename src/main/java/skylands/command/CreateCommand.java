package skylands.command;

import net.minecraft.server.network.ServerPlayerEntity;
import skylands.logic.Island;
import skylands.logic.IslandStuck;
import skylands.logic.Skylands;
import skylands.util.Texts;

public class CreateCommand {

	static void run(ServerPlayerEntity player) {
		IslandStuck islands = Skylands.instance.islands;

		if(islands.get(player).isPresent()) {
			player.sendMessage(Texts.prefixed("message.skylands.island_create.fail"));
		}
		else {
			Island island = islands.create(player);
			island.onFirstLoad();
			player.sendMessage(Texts.prefixed("message.skylands.island_create.success"));
		}
	}
}
