package skylands.util;

import net.minecraft.nbt.NbtCompound;

import java.time.Instant;
import java.util.UUID;

public class NbtMigrator {

	public static void update(NbtCompound nbt) {
		int format = nbt.getInt("format");

		if(format == 0) {
			from0to1(nbt);
			from1to2(nbt);
			from2to3(nbt);
			from3to4(nbt);
		}
		else if(format == 1) {
			from1to2(nbt);
			from2to3(nbt);
			from3to4(nbt);
		}
		else if(format == 2) {
			from2to3(nbt);
			from3to4(nbt);
		}
		else if(format == 3) {
			from3to4(nbt);
		}
	}

	private static void from0to1(NbtCompound nbt) {
		nbt.putInt("format", 1);
		NbtCompound hubNbt = nbt.getCompound("hub");
		hubNbt.putBoolean("hasProtection", false);
		nbt.put("hub", hubNbt);
	}

	private static void from1to2(NbtCompound nbt) {
		nbt.putInt("format", 2);
		NbtCompound islandStuckNbt = nbt.getCompound("islandStuck");
		int size = islandStuckNbt.getInt("size");
		for(int i = 0; i < size; i++) {
			NbtCompound islandNbt = islandStuckNbt.getCompound(String.valueOf(i));
			UUID ownerUuid = islandNbt.getUuid("owner");
			NbtCompound member = new NbtCompound();
			member.putString("name", "");
			member.putUuid("uuid", ownerUuid);
			islandNbt.put("owner", member);

			NbtCompound membersNbt = new NbtCompound();
			membersNbt.putInt("size", 0);
			islandNbt.put("members", membersNbt);
		}
		nbt.put("islandStuck", islandStuckNbt);
	}

	private static void from2to3(NbtCompound nbt) {
		nbt.putInt("format", 3);
		NbtCompound islandStuckNbt = nbt.getCompound("islandStuck");
		int size = islandStuckNbt.getInt("size");
		for(int i = 0; i < size; i++) {
			NbtCompound islandNbt = islandStuckNbt.getCompound(String.valueOf(i));
			islandNbt.putString("created", Instant.now().toString());
		}
		nbt.put("islandStuck", islandStuckNbt);
	}

	private static void from3to4(NbtCompound nbt) {
		nbt.putInt("format", 4);
		NbtCompound islandStuckNbt = nbt.getCompound("islandStuck");
		int size = islandStuckNbt.getInt("size");
		for(int i = 0; i < size; i++) {
			NbtCompound islandNbt = islandStuckNbt.getCompound(String.valueOf(i));
			islandNbt.putBoolean("locked", false);

			NbtCompound spawnPosNbt = new NbtCompound();
			spawnPosNbt.putDouble("x", 0.5D);
			spawnPosNbt.putDouble("y", 75D);
			spawnPosNbt.putDouble("z", 0.5D);
			islandNbt.put("spawnPos", spawnPosNbt);

			NbtCompound visitsPosNbt = new NbtCompound();
			visitsPosNbt.putDouble("x", 0.5D);
			visitsPosNbt.putDouble("y", 75D);
			visitsPosNbt.putDouble("z", 0.5D);
			islandNbt.put("visitsPos", visitsPosNbt);
		}
		nbt.put("islandStuck", islandStuckNbt);
	}
}
