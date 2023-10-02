package skylands.logic;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import skylands.api.SkylandsAPI;
import skylands.config.PlayerPosition;

import java.util.Set;

public class Hub {
	public PlayerPosition spawnPos = Skylands.config.defaultHubPos;
	public boolean hasProtection = Skylands.config.hubProtectedByDefault;

	public Hub() {
	}

	public void readFromNbt(NbtCompound nbt) {
		NbtCompound hubNbt = nbt.getCompound("hub");
		this.spawnPos = PlayerPosition.fromNbt(nbt.getCompound("spawnPos"), new PlayerPosition(hubNbt.getDouble("x"), hubNbt.getDouble("y"), hubNbt.getDouble("z")));
		this.hasProtection = hubNbt.getBoolean("hasProtection");
	}

	public void writeToNbt(NbtCompound nbt) {
		NbtCompound hubNbt = new NbtCompound();

		hubNbt.put("spawnPos", spawnPos.toNbt());

		hubNbt.putBoolean("hasProtection", this.hasProtection);
		nbt.put("hub", hubNbt);
	}

	public void visit(PlayerEntity player) {
		var world = Skylands.getServer().getOverworld();
		player.teleport(world, spawnPos.x, spawnPos.y, spawnPos.z, Set.of(), spawnPos.yaw, spawnPos.pitch);
		SkylandsAPI.ON_HUB_VISIT.invoker().invoke(player, world);
	}
}
