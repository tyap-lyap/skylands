package skylands.command;

import com.mojang.brigadier.CommandDispatcher;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import skylands.logic.Skylands;
import skylands.util.SkylandsTexts;
import skylands.util.SkylandsWorlds;

import static net.minecraft.command.argument.EntityArgumentType.player;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class KickCommand {

	static void init(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("sl").then(literal("kick").requires(Permissions.require("skylands.kick", true)).then(argument("player", player()).executes(context -> {
			var player = context.getSource().getPlayer();
			var kickedPlayer = EntityArgumentType.getPlayer(context, "player");
			if(player != null && kickedPlayer != null) {
				KickCommand.run(player, kickedPlayer);
			}
			return 1;
		}))));
	}

	static void run(ServerPlayerEntity player, ServerPlayerEntity kicked) {
		Skylands.instance.islands.get(player).ifPresentOrElse(island -> {
			if(player.getName().getString().equals(kicked.getName().getString())) {
				player.sendMessage(SkylandsTexts.prefixed("message.skylands.kick_visitor.yourself"));
			}
			else {
				if(island.isMember(kicked)) {
					player.sendMessage(SkylandsTexts.prefixed("message.skylands.kick_visitor.member"));
				}
				else {
					SkylandsWorlds.getIsland(kicked.getWorld()).ifPresent(isl -> {
						if(isl.owner.uuid.equals(island.owner.uuid)) {
							player.sendMessage(SkylandsTexts.prefixed("message.skylands.kick_visitor.success", map -> map.put("%player%", kicked.getName().getString())));

							kicked.sendMessage(SkylandsTexts.prefixed("message.skylands.kick_visitor.kick", map -> map.put("%owner%", player.getName().getString())));
							kicked.sendMessage(SkylandsTexts.prefixed("message.skylands.hub_visit"));
							Skylands.instance.hub.visit(kicked);
						}
						else {
							player.sendMessage(SkylandsTexts.prefixed("message.skylands.kick_visitor.fail", map -> map.put("%player%", kicked.getName().getString())));
						}
					});
				}
			}
		}, () -> player.sendMessage(SkylandsTexts.prefixed("message.skylands.kick_visitor.no_island")));
	}
}
