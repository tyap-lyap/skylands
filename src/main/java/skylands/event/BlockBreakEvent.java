package skylands.event;

import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import skylands.logic.Skylands;
import skylands.util.Texts;
import skylands.util.WorldProtection;

@SuppressWarnings("unused")
public class BlockBreakEvent {

	public static boolean onBreak(World world, PlayerEntity player, BlockPos pos, BlockState state) {
		if(!WorldProtection.canModify(world, player)) {
			player.sendMessage(Texts.prefixed("message.skylands.world_protection.block_break"), true);
			return false;
		}

		if(Skylands.config.rightClickHarvestEnabled && state.getBlock() instanceof CropBlock crop && crop.isMature(state)) {
			player.sendMessage(Texts.prefixed("message.skylands.right_click_harvest.tip"), true);
		}

		return true;
	}
}
