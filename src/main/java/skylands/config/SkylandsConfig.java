package skylands.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import skylands.SkylandsMod;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

public class SkylandsConfig {
	public static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();
	@SuppressWarnings("unused")
	public String readDocs = "https://github.com/tyap-lyap/skylands/wiki";
	public String configFormat = "json";
	public Vec3d defaultSpawnPos = new Vec3d(0.5D, 75.0D, 0.5D);
	public Vec3d defaultVisitsPos = new Vec3d(0.5D, 75.0D, 0.5D);
	public Vec3d defaultHubPos = new Vec3d(0.5D, 80.0D, 0.5D);
	public boolean hubProtectedByDefault = false;
	public int islandDeletionCooldown = (24 * 60) * 60;

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

	public static SkylandsConfig read() {
		String filePath = FabricLoader.getInstance().getConfigDir().resolve("skylands.json").toString();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			return GSON.fromJson(reader, SkylandsConfig.class);
		}
		catch(FileNotFoundException e) {
			SkylandsMod.LOGGER.info("File " + filePath + " is not found! Setting to default.");
			var conf = new SkylandsConfig();
			conf.save();
			return conf;
		}
		catch(Exception e) {
			SkylandsMod.LOGGER.info("Failed to read skylands config due to an exception. " +
					"Please delete skylands.json to regenerate config or fix the issue:\n" + e);
			e.printStackTrace();
			System.exit(0);
			return new SkylandsConfig();
		}
	}

	public void save() {
		try {
			String filePath = FabricLoader.getInstance().getConfigDir().resolve("skylands.json").toString();
			try(FileWriter writer = new FileWriter(filePath)) {
				writer.write(GSON.toJson(this));
			}
		}
		catch(Exception e) {
			SkylandsMod.LOGGER.info("Failed to save skylands config due to an exception:\n" + e);
		}
	}

}
