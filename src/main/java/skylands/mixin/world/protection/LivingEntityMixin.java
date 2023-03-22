package skylands.mixin.world.protection;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import skylands.util.Texts;
import skylands.util.WorldProtection;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

	public LivingEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(method = "damage", at = @At("HEAD"), cancellable = true)
	void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		LivingEntity self = LivingEntity.class.cast(this);

		if(!world.isClient && world.getServer() != null) {
			if(self instanceof PlayerEntity player) {
				if(!WorldProtection.canModify(world, player)) {
					player.sendMessage(Texts.prefixed("message.skylands.world_protection.damage_take"), true);
					cir.setReturnValue(false);
				}
			}
			if(source.getAttacker() instanceof PlayerEntity attacker) {
				if(!WorldProtection.canModify(world, attacker)) {
					attacker.sendMessage(Texts.prefixed("message.skylands.world_protection.entity_hurt"), true);
					cir.setReturnValue(false);
				}
			}
		}
	}
}
