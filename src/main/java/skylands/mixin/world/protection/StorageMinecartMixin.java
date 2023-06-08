package skylands.mixin.world.protection;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import skylands.util.SkylandsTexts;
import skylands.util.WorldProtection;

@Mixin(StorageMinecartEntity.class)
public abstract class StorageMinecartMixin {

	@Inject(method = "interact", at = @At("HEAD"), cancellable = true)
	void interact(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		if(!player.getWorld().isClient) {
			if(!WorldProtection.canModify(player)) {
				player.sendMessage(SkylandsTexts.prefixed("message.skylands.world_protection.minecart_open"), true);
				cir.setReturnValue(ActionResult.FAIL);
			}
		}
	}

}
