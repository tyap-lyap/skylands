package skylands.mixin.world.protection;

import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import skylands.util.SkylandsTexts;
import skylands.util.WorldProtection;

@Mixin(ComparatorBlock.class)
public abstract class ComparatorMixin extends AbstractRedstoneGateBlock {

	protected ComparatorMixin(Settings settings) {
		super(settings);
	}

	@Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
	void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
		if(!world.isClient) {
			if(!WorldProtection.canModify(world, player)) {
				player.sendMessage(SkylandsTexts.prefixed("message.skylands.world_protection.redstone"), true);
				cir.setReturnValue(ActionResult.FAIL);
			}
		}
	}
}
