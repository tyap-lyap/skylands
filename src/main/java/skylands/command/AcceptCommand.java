package skylands.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import skylands.data.SkylandsComponents;
import skylands.logic.Skylands;
import skylands.util.Players;
import skylands.util.Texts;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class AcceptCommand {

	static void init(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("sl").then(literal("accept").requires(Permissions.require("skylands.accept", true)).then(argument("player", word()).executes(context -> {
			String inviter = StringArgumentType.getString(context, "player");
			var player = context.getSource().getPlayer();

			if(player != null) {
				AcceptCommand.run(player, inviter);
			}
			return 1;
		}))));
	}

	static void run(ServerPlayerEntity player, String ownerName) {
		var inviter = Players.get(ownerName);
		if(inviter.isPresent()) {
			var island = Skylands.instance.islands.get(inviter.get());
			if(island.isPresent()) {
				var invite = Skylands.instance.invites.get(island.get(), player);
				if(invite.isPresent()) {
					if(!invite.get().accepted) {
						invite.get().accept(player);
						player.sendMessage(Texts.prefixed("message.skylands.accept.success", map -> map.put("%owner%", ownerName)));
						SkylandsComponents.PLAYER_DATA.get(player).addIsland(ownerName);
					}
				}
				else {
					player.sendMessage(Texts.prefixed("message.skylands.accept.fail"));
				}
			}
			else {
				player.sendMessage(Texts.prefixed("message.skylands.accept.no_island"));
			}
		}
		else {
			player.sendMessage(Texts.prefixed("message.skylands.accept.no_player"));
		}
	}
}
