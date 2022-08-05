package skylands.event;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import skylands.Mod;
import skylands.logic.Skylands;

import java.util.UUID;

public class BlockBreakEvent {

	public static boolean onBreak(World world, PlayerEntity player, BlockPos pos, BlockState state) {
		if(world.getRegistryKey().getValue().getNamespace().equals(Mod.MOD_ID)) {
			var island = Skylands.instance.islandStuck.get(UUID.fromString(world.getRegistryKey().getValue().getPath()));
			if(island.isPresent() && !island.get().isMember(player)) {
				player.sendMessage(Text.of("Skylands > You can't break blocks out here!"));
				return false;
			}
		}
		return true;
	}
}
