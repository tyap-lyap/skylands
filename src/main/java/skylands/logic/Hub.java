package skylands.logic;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;

public class Hub {
	public Vec3d pos = new Vec3d(0, 80, 0);

	public void readFromNbt(NbtCompound nbt) {
		NbtCompound hubNbt = nbt.getCompound("hub");
		this.pos = new Vec3d(hubNbt.getDouble("x"), hubNbt.getDouble("y"), hubNbt.getDouble("z"));
	}

	public void writeToNbt(NbtCompound nbt) {
		NbtCompound hubNbt = new NbtCompound();
		hubNbt.putDouble("x", this.pos.x);
		hubNbt.putDouble("y", this.pos.y);
		hubNbt.putDouble("z", this.pos.z);
		nbt.put("hub", hubNbt);
	}
}
