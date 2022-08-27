package skylands.command;

import net.minecraft.block.Block;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.math.BlockPos;
import skylands.Mod;
import skylands.logic.Island;
import skylands.logic.IslandStuck;
import skylands.logic.Skylands;
import skylands.util.Texts;

public class CreateCommand {

	static void run(ServerPlayerEntity player) {
		MinecraftServer server = Skylands.instance.server;
		IslandStuck islands = Skylands.instance.islandStuck;

		if(islands.get(player).isPresent()) {
			player.sendMessage(Texts.prefixed("message.skylands.island_create.fail"));
		}
		else {
			Island island = islands.create(player);
			ServerWorld world = island.getWorld();
			StructureTemplate structure = server.getStructureTemplateManager().getTemplateOrBlank(Mod.id("main"));
			StructurePlacementData data = new StructurePlacementData().setMirror(BlockMirror.NONE).setIgnoreEntities(true);
			structure.place(world, new BlockPos(-7, 65, -7), new BlockPos(0, 0, 0), data, player.getRandom(), Block.NOTIFY_ALL);
			player.sendMessage(Texts.prefixed("message.skylands.island_create.success"));
		}
	}
}
