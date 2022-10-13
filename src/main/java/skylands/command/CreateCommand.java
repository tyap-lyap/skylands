package skylands.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import skylands.SkylandsMod;
import skylands.logic.Island;
import skylands.logic.IslandStuck;
import skylands.logic.Skylands;
import skylands.task.Tasks;
import skylands.util.Texts;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static net.minecraft.server.command.CommandManager.literal;

public class CreateCommand {

	static void init(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("sl").then(literal("create").executes(context -> {
			var source = context.getSource();
			var player = source.getPlayer();
			if(player != null) {
				CreateCommand.run(player);
			}
			return 1;
		})));
	}

	static void run(ServerPlayerEntity player) {
		IslandStuck islands = Skylands.instance.islands;

		if(islands.get(player).isPresent()) {
			player.sendMessage(Texts.prefixed("message.skylands.island_create.fail"));
		}
		else {
			SkylandsMod.LOGGER.info("Creating a new Island for " + player.getName().getString() + "...");
			Instant inst = Instant.now();
			Island island = islands.create(player);

			// todo: User progress notifier
			island.onFirstLoadAsync().thenRun(() -> {
				SkylandsMod.LOGGER.info("Island got successfully created in " + ChronoUnit.MILLIS.between(inst, Instant.now()) + " ms");
				player.sendMessage(Texts.prefixed("message.skylands.island_create.success"));
				Tasks.INSTANCE.nextTick().thenRun(() -> {
					island.visitAsMember(player);
				});
			});
		}
	}
}
