package skylands.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import skylands.logic.Island;
import skylands.logic.Skylands;

import java.util.ArrayList;

public class WorldDataComponent implements AbstractWorldData {
	public World world;

	public WorldDataComponent(World world) {
		this.world = world;
	}

	@Override
	public void readFromNbt(NbtCompound nbt) {
		if(world.getRegistryKey().equals(World.OVERWORLD)) {
			NbtCompound islandStuckNbt = nbt.getCompound("islandStuck");
			int size = islandStuckNbt.getInt("size");
			for(int i = 0; i < size; i++) {
				NbtCompound islandNbt = islandStuckNbt.getCompound(String.valueOf(i));
				Island island = new Island(islandNbt.getUuid("owner"));
				if(!Skylands.instance.islandStuck.hasIsland(island.ownerUUID)) {
					Skylands.instance.islandStuck.islands.add(island);
					island.getWorld();
				}
			}
		}
	}

	@Override
	public void writeToNbt(NbtCompound nbt) {
		if(world.getRegistryKey().equals(World.OVERWORLD)) {
			NbtCompound islandStuckNbt = new NbtCompound();
			ArrayList<Island> islands = Skylands.instance.islandStuck.islands;
			islandStuckNbt.putInt("size", islands.size());
			for(int i = 0; i < islands.size(); i++) {
				Island island = islands.get(i);
				NbtCompound islandNbt = new NbtCompound();
				islandNbt.putUuid("owner", island.ownerUUID);
				islandStuckNbt.put(Integer.toString(i), islandNbt);
			}
			nbt.put("islandStuck", islandStuckNbt);
		}

	}
}
