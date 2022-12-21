package skylands.config;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;

public class SkylandsConfig {
	public Vec3d defaultSpawnPos = new Vec3d(0.5D, 75D, 0.5D);
	public Vec3d defaultVisitsPos = new Vec3d(0.5D, 75D, 0.5D);

	public boolean updateCheckerEnabled = true;
	public boolean teleportAfterIslandCreation = false;
	public boolean createIslandOnPlayerJoin = false;
	public boolean rightClickHarvestEnabled = true;

	public void readFromNbt(NbtCompound nbt) {
		NbtCompound configNbt = nbt.getCompound("config");
		if(configNbt.isEmpty()) return;

		var spawnPosNbt = configNbt.getCompound("defaultSpawnPos");
		double spawnPosX = spawnPosNbt.getDouble("x");
		double spawnPosY = spawnPosNbt.getDouble("y");
		double spawnPosZ = spawnPosNbt.getDouble("z");
		this.defaultSpawnPos = new Vec3d(spawnPosX, spawnPosY, spawnPosZ);

		var visitsPosNbt = configNbt.getCompound("defaultVisitsPos");
		double visitsPosX = visitsPosNbt.getDouble("x");
		double visitsPosY = visitsPosNbt.getDouble("y");
		double visitsPosZ = visitsPosNbt.getDouble("z");
		this.defaultVisitsPos = new Vec3d(visitsPosX, visitsPosY, visitsPosZ);

		this.updateCheckerEnabled = configNbt.getBoolean("updateCheckerEnabled");
		this.teleportAfterIslandCreation = configNbt.getBoolean("teleportAfterIslandCreation");
		this.createIslandOnPlayerJoin = configNbt.getBoolean("createIslandOnPlayerJoin");
		this.rightClickHarvestEnabled = configNbt.getBoolean("rightClickHarvestEnabled");
	}

	public void writeToNbt(NbtCompound nbt) {
		NbtCompound configNbt = new NbtCompound();

		NbtCompound spawnPosNbt = new NbtCompound();
		spawnPosNbt.putDouble("x", this.defaultSpawnPos.getX());
		spawnPosNbt.putDouble("y", this.defaultSpawnPos.getY());
		spawnPosNbt.putDouble("z", this.defaultSpawnPos.getZ());
		configNbt.put("defaultSpawnPos", spawnPosNbt);

		NbtCompound visitsPosNbt = new NbtCompound();
		visitsPosNbt.putDouble("x", this.defaultVisitsPos.getX());
		visitsPosNbt.putDouble("y", this.defaultVisitsPos.getY());
		visitsPosNbt.putDouble("z", this.defaultVisitsPos.getZ());
		configNbt.put("defaultVisitsPos", visitsPosNbt);

		configNbt.putBoolean("updateCheckerEnabled", this.updateCheckerEnabled);
		configNbt.putBoolean("teleportAfterIslandCreation", this.teleportAfterIslandCreation);
		configNbt.putBoolean("createIslandOnPlayerJoin", this.createIslandOnPlayerJoin);
		configNbt.putBoolean("rightClickHarvestEnabled", this.rightClickHarvestEnabled);

		nbt.put("config", configNbt);
	}
}
