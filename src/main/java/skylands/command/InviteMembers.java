package skylands.command;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import skylands.logic.Skylands;

public class InviteMembers {

	static void add(ServerPlayerEntity inviter, ServerPlayerEntity newcomer) {
		Skylands.instance.islandStuck.get(inviter).ifPresentOrElse(island -> {
			if(island.isMember(newcomer)) {
				inviter.sendMessage(Text.of("Skylands > This player is already member of your island!"));
			}
			else {
				if(Skylands.instance.invites.hasInvite(island, newcomer)) {
					inviter.sendMessage(Text.of("Skylands > You have already invited this player!"));
				}
				else {
					inviter.sendMessage(Text.of("Skylands > " + newcomer.getName().getString() + " got successfully invited! They got 5 minutes to accept your invite."));

					Style style = Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sl accept" + inviter.getName().getString()));
					style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("Click here to accept this invite or type \"/sl accept " + inviter.getName().getString() + "\" command.")));

					newcomer.sendMessage(Text.literal("Skylands > " + inviter.getName().getString() + " wants you to join their Island!").fillStyle(style));
					newcomer.sendMessage(Text.literal("Skylands > Click here to accept this invite.").fillStyle(style));
					Skylands.instance.invites.create(island, newcomer);
				}
			}
		}, () -> inviter.sendMessage(Text.of("Skylands > You don't have an island yet!")));
	}

	static void remove(ServerPlayerEntity player, String removed) {
		Skylands.instance.islandStuck.get(player).ifPresentOrElse(island -> {
			if(player.getName().getString().equals(removed)) {
				player.sendMessage(Text.of("Skylands > You can't remove yourself."));
			}
			else {
				if(island.isMember(removed)) {
					island.members.removeIf(member -> member.name.equals(removed));
					player.sendMessage(Text.of("Skylands > " + removed + " got successfully removed."));
				}
				else {
					player.sendMessage(Text.of("Skylands > This player is not member of your island."));
				}
			}
		}, () -> player.sendMessage(Text.of("Skylands > You don't have an island yet!")));
	}
}
