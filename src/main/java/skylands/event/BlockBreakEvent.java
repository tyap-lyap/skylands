package skylands.event;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import skylands.util.WorldProtection;

@SuppressWarnings("unused")
public class BlockBreakEvent {

	public static boolean onBreak(World world, PlayerEntity player, BlockPos pos, BlockState state) {
		if(!WorldProtection.canModify(world, player)) {
			player.sendMessage(Text.of("Skylands > You can't break blocks out here!"), true);
			return false;
		}
		return true;
	}
}
