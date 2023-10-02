package skylands.config;

import com.google.gson.annotations.JsonAdapter;
import net.minecraft.block.Block;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class Template {
	public String name;
	public String type;
	public Metadata metadata;
	@JsonAdapter(PlayerPosition.JsonAdapter.class)
	public PlayerPosition playerSpawnPosition;

	@JsonAdapter(PlayerPosition.JsonAdapter.class) @Nullable
	public PlayerPosition playerVisitsPosition;

	public Template(String name, String type, Metadata metadata, PlayerPosition playerSpawnPosition) {
		this.name = name;
		this.type = type;
		this.metadata = metadata;
		this.playerSpawnPosition = playerSpawnPosition;
	}

	public void generateStructure(ServerWorld world) {
		if(type.equals("structure")) {
			StructureTemplate structure = world.getServer().getStructureTemplateManager().getTemplateOrBlank(new Identifier(metadata.structure));
			StructurePlacementData data = new StructurePlacementData().setMirror(BlockMirror.NONE).setRotation(metadata.getRotation()).setIgnoreEntities(true);
			structure.place(world, metadata.position.toBlockPos(), metadata.getPivot(), data, world.getRandom(), Block.NOTIFY_ALL);
		}
	}

	public PlayerPosition getPlayerVisitsPosition() {
		if(playerVisitsPosition != null) {
			return playerVisitsPosition;
		}

		return playerSpawnPosition;
	}
}
