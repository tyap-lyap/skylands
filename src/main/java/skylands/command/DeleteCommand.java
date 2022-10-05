package skylands.command;

import net.minecraft.server.network.ServerPlayerEntity;
import skylands.logic.IslandStuck;
import skylands.logic.Skylands;
import skylands.util.Texts;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class DeleteCommand {

	static void run(ServerPlayerEntity player) {
		IslandStuck islands = Skylands.instance.islands;

		islands.get(player).ifPresentOrElse(island -> {
			var created = island.created;
			var now = Instant.now();
			var hours = ChronoUnit.HOURS.between(created, now);

			if(hours >= 24) {
				islands.delete(player);
				player.sendMessage(Texts.prefixed("message.skylands.island_delete.success"));
			}
			else {
				player.sendMessage(Texts.prefixed("message.skylands.island_delete.too_often"));
			}

		}, () -> {
			player.sendMessage(Texts.prefixed("message.skylands.island_delete.fail"));
		});
	}
}
