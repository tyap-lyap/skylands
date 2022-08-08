package skylands.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

@SuppressWarnings("unused")
public class UseItemEvent {

	public static TypedActionResult<ItemStack> onUse(PlayerEntity player, World world, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);
		return TypedActionResult.pass(stack);
	}
}
