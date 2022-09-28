package skylands.logic;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import skylands.util.NbtMigrator;
import xyz.nucleoid.fantasy.Fantasy;

public class Skylands {
	public int format = 2;
	public static Skylands instance;
	public MinecraftServer server;
	public Fantasy fantasy;
	public IslandStuck islandStuck;
	public Hub hub;
	public Invites invites;

	public Skylands(MinecraftServer server) {
		this.server = server;
		this.fantasy = Fantasy.get(server);
		this.islandStuck = new IslandStuck();
		this.hub = new Hub();
		this.invites = new Invites();
	}

	public void readFromNbt(NbtCompound nbt) {
		NbtCompound skylandsNbt = nbt.getCompound("skylands");
		if(skylandsNbt.isEmpty()) return;

		NbtMigrator.update(skylandsNbt);

		this.format = skylandsNbt.getInt("format");
		this.islandStuck.readFromNbt(skylandsNbt);
		this.hub.readFromNbt(skylandsNbt);
	}

	public void writeToNbt(NbtCompound nbt) {
		NbtCompound skylandsNbt = new NbtCompound();

		skylandsNbt.putInt("format", this.format);
		this.islandStuck.writeToNbt(skylandsNbt);
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
