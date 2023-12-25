package skylands.config.template;

import com.google.gson.annotations.JsonAdapter;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import skylands.config.BlockPosition;

public class Metadata {

	public String structure;
	@JsonAdapter(BlockPosition.JsonAdapter.class)
	public BlockPosition position;
	@Nullable
	public BlockPosition pivot;
	@Nullable
	public String rotation;

	public String path;

	public Metadata(String structureId, BlockPosition position) {
		this.structure = structureId;
		this.position = position;
	}

	public Metadata(String path) {
		this.path = path;
	}

	public BlockRotation getRotation() {
		BlockRotation rot = BlockRotation.NONE;

		if(rotation != null) {
			switch (rotation.toLowerCase()) {
				case "clockwise_90" -> rot = BlockRotation.CLOCKWISE_90;
				case "180" -> rot = BlockRotation.CLOCKWISE_180;
				case "counterclockwise_90" -> rot = BlockRotation.COUNTERCLOCKWISE_90;
			}
		}

		return rot;
	}

	public BlockPos getPivot() {
		if(pivot != null) {
			return pivot.toBlockPos();
		}
		return new BlockPos(0, 0, 0);
	}

}
