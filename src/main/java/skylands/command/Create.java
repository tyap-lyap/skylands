package skylands.command;

import net.minecraft.block.Block;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.text.Text;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.math.BlockPos;
import skylands.Mod;
import skylands.logic.Island;
import skylands.logic.IslandStuck;
import skylands.logic.Skylands;

public class Create {

	static void cmd(ServerPlayerEntity player) {
		MinecraftServer server = Skylands.instance.server;
		IslandStuck islands = Skylands.instance.islandStuck;

		if(islands.get(player).isPresent()) {
			player.sendMessage(Text.of("Skylands > You're already have an island!"));
		}
		else {
			Island island = islands.create(player);
			ServerWorld world = island.getWorld();
			StructureTemplate structure = server.getStructureTemplateManager().getTemplateOrBlank(Mod.id("main"));
			StructurePlacementData data = new StructurePlacementData().setMirror(BlockMirror.NONE).setIgnoreEntities(true);
			structure.place(world, new BlockPos(-7, 65, -7), new BlockPos(0, 0, 0), data, player.getRandom(), Block.NOTIFY_ALL);
//			player.setSpawnPoint(world.getRegistryKey(), new BlockPos(island.spawnPos), 0, false, false);
			player.sendMessage(Text.of("Skylands > Your island got successfully created, you can now visit it with the \"/sl home\" command!"));
		}
	}
}
