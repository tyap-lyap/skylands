package skylands.config.template;

import com.google.gson.annotations.JsonAdapter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import skylands.config.PlayerPosition;

public class Template {
	public String name;
	public String type;
	public Metadata metadata;
	@Nullable public PlayerPosition spawnPosition;
	@Nullable public PlayerPosition visitsPosition;

	public String permission;

	public Template(String name, String type, Metadata metadata) {
		this.name = name;
		this.type = type;
		this.metadata = metadata;
	}

	public Template(String name, String type, Metadata metadata, PlayerPosition spawnPosition) {
		this.name = name;
		this.type = type;
		this.metadata = metadata;
		this.spawnPosition = spawnPosition;
	}
	public Template(String name, String type, Metadata metadata, PlayerPosition spawnPosition, PlayerPosition visitsPosition) {
		this.name = name;
		this.type = type;
		this.metadata = metadata;
		this.spawnPosition = spawnPosition;
		this.visitsPosition = visitsPosition;
	}

	public void generateStructure(ServerWorld world) {
		if(type.equals("structure")) {
			StructureTemplate structure = world.getServer().getStructureTemplateManager().getTemplateOrBlank(new Identifier(metadata.structure));
			StructurePlacementData data = new StructurePlacementData().setMirror(BlockMirror.NONE).setRotation(metadata.getRotation()).setIgnoreEntities(true);
			structure.place(world, metadata.position.toBlockPos(), metadata.getPivot(), data, world.getRandom(), Block.NOTIFY_ALL);

			if(FabricLoader.getInstance().isDevelopmentEnvironment()) {
				world.setBlockState(metadata.position.toBlockPos().down(), Blocks.STRUCTURE_BLOCK.getDefaultState());

				var structureBlock = (StructureBlockBlockEntity)world.getBlockEntity(metadata.position.toBlockPos().down());
				structureBlock.setTemplateName(new Identifier(metadata.structure));
				structureBlock.setMode(StructureBlockMode.LOAD);
				structureBlock.loadStructure(world);
			}
		}
	}
}
