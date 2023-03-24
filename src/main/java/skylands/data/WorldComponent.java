package skylands.data;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import skylands.logic.Skylands;

public class WorldComponent implements ComponentV3 {
	public World world;

	public WorldComponent(World world) {
		this.world = world;
	}

	@Override
	public void readFromNbt(@NotNull NbtCompound nbt) {
		if(world.getRegistryKey().equals(World.OVERWORLD)) {
			Skylands.instance.readFromNbt(nbt);
		}
	}

	@Override
	public void writeToNbt(@NotNull NbtCompound nbt) {
		if(world.getRegistryKey().equals(World.OVERWORLD)) {
			Skylands.instance.writeToNbt(nbt);
		}
	}
}
