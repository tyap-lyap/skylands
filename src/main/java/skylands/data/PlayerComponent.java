package skylands.data;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import skylands.logic.Skylands;
import skylands.util.Worlds;

import java.util.ArrayList;
import java.util.UUID;

public class PlayerComponent implements ComponentV3 {

	public PlayerEntity player;

	ArrayList<String> islands = new ArrayList<>();

	public PlayerComponent(PlayerEntity player) {
		this.player = player;
	}

	public ArrayList<String> getIslands() {
		return islands;
	}

	public void setIslands(ArrayList<String> islands) {
		this.islands = islands;
	}

	public void addIsland(String owner) {
		if(!this.islands.contains(owner)) {
			islands.add(owner);
		}
	}

	public void removeIsland(String owner) {
		islands.removeIf(s -> s.equals(owner));
	}

	@Override
	public void readFromNbt(NbtCompound tag) {
		NbtCompound islandsNbt = tag.getCompound("islands");
		int size = islandsNbt.getInt("size");
		for(int i = 0; i < size; i++) {
			String owner = islandsNbt.getString(String.valueOf(i));
			this.islands.add(owner);
		}

		if(!tag.getString("lastIsland").isEmpty()) {
			Skylands.instance.islands.get(UUID.fromString(tag.getString("lastIsland"))).ifPresent(island -> {
				island.getWorld();
				if(island.hasNether) island.getNether();
			});
		}
	}

	@Override
	public void writeToNbt(NbtCompound tag) {
		NbtCompound islandsNbt = new NbtCompound();
		islandsNbt.putInt("size", this.islands.size());
		for(int i = 0; i < this.islands.size(); i++) {
			String owner = this.islands.get(i);
			islandsNbt.putString(Integer.toString(i), owner);
		}
		tag.put("islands", islandsNbt);

		Worlds.getIsland(player.getWorld()).ifPresent(island -> {
			tag.putString("lastIsland", island.owner.uuid.toString());
		});
	}
}
