package skylands.mixin.world.protection;

import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import skylands.util.Texts;
import skylands.util.WorldProtection;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin {

	@Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
	void interact(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		if(!player.world.isClient) {
			if(!WorldProtection.canModify(player.world, player)) {
				player.sendMessage(Texts.prefixed("message.skylands.world_protection.villager_use"), true);
				cir.setReturnValue(ActionResult.FAIL);
			}
		}
	}

}
