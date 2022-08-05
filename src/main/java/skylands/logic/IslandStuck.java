package skylands.logic;

import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class IslandStuck {
	public ArrayList<Island> islands = new ArrayList<>();

	public Island create(PlayerEntity player) {
		for(var island : this.islands) {
			if(island.ownerUUID.equals(player.getUuid())) return island;
		}
		var island = new Island(player.getUuid());
		this.islands.add(island);
		return island;
	}

	public Optional<Island> get(PlayerEntity player) {
		for(var island : this.islands) {
			if(island.ownerUUID.equals(player.getUuid())) return Optional.of(island);
		}
		return Optional.empty();
	}

	public Optional<Island> get(UUID uuid) {
		for(var island : this.islands) {
			if(island.ownerUUID.equals(uuid)) return Optional.of(island);
		}
		return Optional.empty();
	}

	public boolean hasIsland(UUID uuid) {
		for(var island : this.islands) {
			if(island.ownerUUID.equals(uuid)) return true;
		}
		return false;
	}
}
