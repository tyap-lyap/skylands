package skylands.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import skylands.SkylandsMod;
import skylands.logic.Skylands;
import skylands.util.Texts;

import static net.minecraft.command.argument.EntityArgumentType.player;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class VisitCommand {

	static void init(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("sl").then(literal("visit").then(argument("player", player()).executes(context -> {
			var visitor = context.getSource().getPlayer();
			var owner = EntityArgumentType.getPlayer(context, "player");
			if(visitor != null && owner != null) {
				VisitCommand.run(visitor, owner);
			}
			return 1;
		}))));
	}

	static void run(ServerPlayerEntity visitor, ServerPlayerEntity owner) {
		String ownerName = owner.getName().getString();

		Skylands.instance.islands.get(owner).ifPresentOrElse(island -> {
			if(!island.isMember(visitor) && island.isBanned(visitor)) {
				visitor.sendMessage(Texts.prefixed("message.skylands.island_visit.ban", map -> map.put("%owner%", ownerName)));
			}
			else {
				if(!island.locked) {
					if(visitor.getWorld().getRegistryKey().getValue().equals(SkylandsMod.id(island.owner.uuid.toString()))) {
						visitor.sendMessage(Texts.prefixed("message.skylands.island_visit.fail", map -> map.put("%owner%", ownerName)));
					}
					else {
						visitor.sendMessage(Texts.prefixed("message.skylands.island_visit.success", map -> map.put("%owner%", ownerName)));
						island.visitAsVisitor(visitor);
					}
				}
				else {
					visitor.sendMessage(Texts.prefixed("message.skylands.island_visit.no_visits", map -> map.put("%owner%", ownerName)));
				}
			}

		}, () -> visitor.sendMessage(Texts.prefixed("message.skylands.island_visit.no_island", map -> map.put("%owner%", ownerName))));
	}
}
