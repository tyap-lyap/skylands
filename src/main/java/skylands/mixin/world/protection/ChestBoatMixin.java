package skylands.mixin.world.protection;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.ChestBoatEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import skylands.util.SkylandsTexts;
import skylands.util.WorldProtection;

@Mixin(ChestBoatEntity.class)
public abstract class ChestBoatMixin extends BoatEntity {

	public ChestBoatMixin(EntityType<? extends BoatEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "interact", at = @At("HEAD"), cancellable = true)
	void interact(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		if(!player.getWorld().isClient) {
			if(!WorldProtection.canModify(player.getWorld(), player)) {
				player.sendMessage(SkylandsTexts.prefixed("message.skylands.world_protection.boat_open"), true);
				cir.setReturnValue(ActionResult.FAIL);
			}
		}
	}
}
