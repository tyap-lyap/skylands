package skylands.logic;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import skylands.config.SkylandsConfig;
import skylands.util.NbtMigrator;
import xyz.nucleoid.fantasy.Fantasy;

public class Skylands {
	public int format = 4;
	public static Skylands instance;
	public MinecraftServer server;
	public Fantasy fantasy;
	public IslandStuck islands;
	public Hub hub;
	public Invites invites;

	public static SkylandsConfig config;

	public Skylands(MinecraftServer server) {
		this.server = server;
		this.fantasy = Fantasy.get(server);
		this.islands = new IslandStuck();
		this.hub = new Hub();
		this.invites = new Invites();
	}

	public void readFromNbt(NbtCompound nbt) {
		NbtCompound skylandsNbt = nbt.getCompound("skylands");
		if(skylandsNbt.isEmpty()) return;

		NbtMigrator.update(skylandsNbt);

		this.format = skylandsNbt.getInt("format");
		this.islands.readFromNbt(skylandsNbt);
		this.hub.readFromNbt(skylandsNbt);
	}

	public void writeToNbt(NbtCompound nbt) {
		NbtCompound skylandsNbt = new NbtCompound();

		skylandsNbt.putInt("format", this.format);
		this.islands.writeToNbt(skylandsNbt);
		this.hub.writeToNbt(skylandsNbt);

		nbt.put("skylands", skylandsNbt);
	}

	public static MinecraftServer getServer() {
		return getInstance().server;
	}

	public static IslandStuck getIslands() {
		return getInstance().islands;
	}

	public static Hub getHub() {
		return getInstance().hub;
	}

	public static Skylands getInstance() {
		return Skylands.instance;
	}

	public void onTick(MinecraftServer server) {
		this.invites.tick(server);
	}

}
