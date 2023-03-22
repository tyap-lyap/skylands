package skylands.logic;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;

import java.util.Set;

public class Hub {
	public Vec3d pos = Skylands.config.defaultHubPos;
	public boolean hasProtection = Skylands.config.hubProtectedByDefault;

	public Hub() {
	}

	public void readFromNbt(NbtCompound nbt) {
		NbtCompound hubNbt = nbt.getCompound("hub");
		this.pos = new Vec3d(hubNbt.getDouble("x"), hubNbt.getDouble("y"), hubNbt.getDouble("z"));
		this.hasProtection = hubNbt.getBoolean("hasProtection");
	}

	public void writeToNbt(NbtCompound nbt) {
		NbtCompound hubNbt = new NbtCompound();
		hubNbt.putDouble("x", this.pos.x);
		hubNbt.putDouble("y", this.pos.y);
		hubNbt.putDouble("z", this.pos.z);
		hubNbt.putBoolean("hasProtection", this.hasProtection);
		nbt.put("hub", hubNbt);
	}

	public void visit(PlayerEntity player) {
		player.teleport(Skylands.instance.server.getOverworld(), pos.getX(), pos.getY(), pos.getZ(), Set.of(), 0, 0);
	}
}
