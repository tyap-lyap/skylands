package skylands.logic;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import skylands.util.Texts;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class Invites {
	public ArrayList<Invite> invites = new ArrayList<>();

	public void tick(MinecraftServer server) {
		invites.forEach(Invite::tick);
		invites.removeIf(invite -> invite.ticks == 0 || invite.accepted);
	}

	public void create(Island island, PlayerEntity player) {
		invites.add(new Invite(island, player.getUuid()));
	}

	public Optional<Invite> get(Island island, PlayerEntity player) {
		for(var invite : this.invites) {
			if(invite.island.equals(island) && invite.uuid.equals(player.getUuid())) return Optional.of(invite);
		}
		return Optional.empty();
	}

	public boolean hasInvite(Island island, PlayerEntity player) {
		for(var invite : this.invites) {
			if(invite.island.equals(island) && invite.uuid.equals(player.getUuid())) return true;
		}
		return false;
	}

	public static class Invite {
		public UUID uuid;
		public Island island;

		public boolean accepted = false;
		public int ticks = (60 * 20) * 5;

		public Invite(Island island, UUID uuid) {
			this.uuid = uuid;
			this.island = island;
		}

		public void tick() {
			if(ticks != 0) {
				ticks--;
			}
		}

		public void accept(PlayerEntity player) {
			if(island != null) {
				this.accepted = true;
				island.members.add(new Member(player));
			}
			else {
				player.sendMessage(Texts.prefixed("message.skylands.accept.no_island"));
			}
		}
	}
}
