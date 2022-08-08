package skylands.util;

import net.minecraft.nbt.NbtCompound;

public class NbtMigrator {

	public static void update(NbtCompound nbt) {
		int format = nbt.getInt("format");

		if(format == 0) {
			from0to1(nbt);
		}
	}

	private static void from0to1(NbtCompound nbt) {
		nbt.putInt("format", 1);
		NbtCompound hubNbt = nbt.getCompound("hub");
		hubNbt.putBoolean("hasProtection", false);
		nbt.put("hub", hubNbt);
	}
}
