package skylands.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;

import java.io.IOException;

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

	public NbtCompound toNbt() {
		NbtCompound nbt = new NbtCompound();
		nbt.putDouble("x", this.x);
		nbt.putDouble("y", this.y);
		nbt.putDouble("z", this.z);
		nbt.putFloat("yaw", this.yaw);
		nbt.putFloat("pitch", this.pitch);
		return nbt;
	}

	public static PlayerPosition fromNbt(NbtCompound nbt) {
		PlayerPosition pos = new PlayerPosition(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"));
		if (nbt.contains("yaw")) pos.yaw = nbt.getFloat("yaw");
		if (nbt.contains("pitch")) pos.yaw = nbt.getFloat("pitch");
		return pos;
	}

	public static PlayerPosition fromNbt(NbtCompound nbt, PlayerPosition backup) {
		PlayerPosition pos = new PlayerPosition(0, 0, 0);

		if(nbt.contains("x")) pos.x = nbt.getDouble("x");
		else pos.x = backup.x;

		if(nbt.contains("y")) pos.y = nbt.getDouble("y");
		else pos.y = backup.y;

		if(nbt.contains("z")) pos.z = nbt.getDouble("z");
		else pos.z = backup.z;

		if (nbt.contains("yaw")) pos.yaw = nbt.getFloat("yaw");
		if (nbt.contains("pitch")) pos.yaw = nbt.getFloat("pitch");
		return pos;
	}

	public static class JsonAdapter extends TypeAdapter<PlayerPosition> {

		public static final Gson GSON = new GsonBuilder().setLenient().create();

		@Override
		public void write(JsonWriter out, PlayerPosition value) throws IOException {
			out.jsonValue(GSON.toJson(value).replaceAll(",", ", ")
				.replaceAll("\\{", "{ ")
				.replaceAll("}", " }").replaceAll(":", ": "));
		}

		@Override
		public PlayerPosition read(JsonReader in) {
			return GSON.fromJson(in, PlayerPosition.class);
		}
	}
}
