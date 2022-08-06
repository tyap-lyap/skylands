package skylands.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import skylands.logic.Skylands;

public class WorldComponent implements AbstractWorldData {
	public World world;

	public WorldComponent(World world) {
		this.world = world;
	}

	@Override
	public void readFromNbt(NbtCompound nbt) {
		if(world.getRegistryKey().equals(World.OVERWORLD)) {
			Skylands.instance.readFromNbt(nbt);
		}
	}

	@Override
	public void writeToNbt(NbtCompound nbt) {
		if(world.getRegistryKey().equals(World.OVERWORLD)) {
			Skylands.instance.writeToNbt(nbt);
		}
	}
}
