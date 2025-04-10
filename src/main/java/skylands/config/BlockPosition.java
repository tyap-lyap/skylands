package skylands.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.io.IOException;

public class BlockPosition {
	public int x;
	public int y;
	public int z;

	public BlockPosition(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vec3i toVec() {
		return new Vec3i(this.x, this.y, this.z);
	}

	public BlockPos toBlockPos() {
		return new BlockPos(x, y, z);
	}

	public static class JsonAdapter extends TypeAdapter<BlockPosition> {

		public static final Gson GSON = new GsonBuilder().setLenient().create();

		@Override
		public void write(JsonWriter out, BlockPosition value) throws IOException {
			out.jsonValue(GSON.toJson(value).replaceAll(",", ", ")
				.replaceAll("\\{", "{ ")
				.replaceAll("}", " }").replaceAll(":", ": "));
		}

		@Override
		public BlockPosition read(JsonReader in) {
			return GSON.fromJson(in, BlockPosition.class);
		}
	}
}
