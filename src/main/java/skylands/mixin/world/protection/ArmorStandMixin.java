package skylands.mixin.world.protection;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import skylands.util.Texts;
import skylands.util.WorldProtection;

@Mixin(ArmorStandEntity.class)
public abstract class ArmorStandMixin extends LivingEntity {

	protected ArmorStandMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "damage", at = @At("HEAD"), cancellable = true)
	void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		if(!world.isClient && source.getAttacker() instanceof PlayerEntity attacker) {
			if(!WorldProtection.canModify(world, attacker)) {
				attacker.sendMessage(Texts.prefixed("message.skylands.world_protection.entity_hurt"), true);
				cir.setReturnValue(false);
			}
		}
	}
}
