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
	public SkylandsConfig config;

	public Skylands(MinecraftServer server) {
		this.server = server;
		this.fantasy = Fantasy.get(server);
		this.islands = new IslandStuck();
		this.hub = new Hub();
		this.invites = new Invites();
		this.config = new SkylandsConfig();
	}

	public void readFromNbt(NbtCompound nbt) {
		NbtCompound skylandsNbt = nbt.getCompound("skylands");
		if(skylandsNbt.isEmpty()) return;

		NbtMigrator.update(skylandsNbt);

		this.format = skylandsNbt.getInt("format");
		this.config.readFromNbt(skylandsNbt);
		this.islands.readFromNbt(skylandsNbt);
		this.hub.readFromNbt(skylandsNbt);
	}

	public void writeToNbt(NbtCompound nbt) {
		NbtCompound skylandsNbt = new NbtCompound();

		skylandsNbt.putInt("format", this.format);
		this.config.writeToNbt(skylandsNbt);
		this.islands.writeToNbt(skylandsNbt);
		this.hub.writeToNbt(skylandsNbt);

		nbt.put("skylands", skylandsNbt);
	}

	public static Skylands getInstance() {
		return Skylands.instance;
	}

	public void onTick(MinecraftServer server) {
		this.invites.tick(server);
	}

}
