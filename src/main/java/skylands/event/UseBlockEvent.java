package skylands.event;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import skylands.logic.Skylands;
import skylands.util.Texts;
import skylands.util.WorldProtection;

public class UseBlockEvent {

	public static ActionResult onBlockUse(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
		BlockPos pos = hitResult.getBlockPos();
		BlockState state = world.getBlockState(pos);
		ItemStack toolStack = player.getStackInHand(hand);

		if(state.getBlock() instanceof CropBlock crop && crop.isMature(state)) {
			if(!Skylands.config.rightClickHarvestEnabled) return ActionResult.PASS;

			if(WorldProtection.canModify(world, player)) {
				Item replant = state.getBlock().getPickStack(world, pos, state).getItem();
				final boolean[] removedReplant = {false};

				Block.getDroppedStacks(state, (ServerWorld)world, pos, null, player, toolStack).forEach(stack -> {
					if (!removedReplant[0] && stack.getItem() == replant) {
						stack.setCount(stack.getCount() - 1);
						removedReplant[0] = true;
					}

					Block.dropStack(world, pos, stack);
				});

				state.onStacksDropped((ServerWorld)world, pos, toolStack, true);
				world.setBlockState(pos, crop.withAge(0));

				return ActionResult.SUCCESS;
			}
			else {
				player.sendMessage(Texts.prefixed("message.skylands.world_protection.harvest"), true);
				return ActionResult.PASS;
			}

		}

		return ActionResult.PASS;
	}
}
