package skylands.config;

import net.minecraft.util.math.Vec3d;

public class PlayerPosition {
	public double x;
	public double y;
	public double z;

	public float yaw = 0;
	public float pitch = 0;

	public PlayerPosition(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public PlayerPosition(double x, double y, double z, float yaw, float pitch) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public Vec3d toVec() {
		return new Vec3d(this.x, this.y, this.z);
	}
}
