package skylands.command;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class Help {
	static String text = """
						- /sl hub -> Teleport to the hub.
						- /sl create -> Creates an island.
						- /sl home -> Teleport to your island.
						- /sl visit <player> -> Visit someone's island.
						- /sl home <player> -> Teleport to an island you are member of.
						- /sl add-member <player> -> Invite player to your island.
						- /sl remove-member <player> -> Remove player from your island.
						- /sl accept <player> -> Accept island join invite request.
						- /sl kick <player> -> Kick player from your island.
						- /sl ban <player> -> Ban player from visiting your island.
						- /sl help -> Sends this list.""";

	static void cmd(ServerPlayerEntity player) {
		player.sendMessage(Text.of(text));
	}
}
