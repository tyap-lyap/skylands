package skylands.logic;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.UUID;

public class Member {
	public String name;
	public UUID uuid;

	public Member(UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;
	}

	public Member(PlayerEntity player) {
		this.uuid = player.getUuid();
		this.name = player.getName().getString();
	}

	public static Member fromNbt(NbtCompound nbt) {
		return new Member(nbt.getUuid("uuid"), nbt.getString("name"));
	}

	public NbtCompound toNbt() {
		NbtCompound nbt = new NbtCompound();
		nbt.putString("name", this.name);
		nbt.putUuid("uuid", this.uuid);
		return nbt;
	}
}
