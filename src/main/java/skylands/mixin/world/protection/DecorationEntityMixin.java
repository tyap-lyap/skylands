package skylands.mixin.world.protection;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import skylands.util.SkylandsTexts;
import skylands.util.WorldProtection;

@Mixin(AbstractDecorationEntity.class)
public abstract class DecorationEntityMixin extends Entity {

	public DecorationEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(method = "damage", at = @At("HEAD"), cancellable = true)
	void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		World world = getWorld();
		if(!world.isClient && source.getAttacker() instanceof PlayerEntity attacker) {
			if(!WorldProtection.canModify(world, attacker)) {
				attacker.sendMessage(SkylandsTexts.prefixed("message.skylands.world_protection.entity_hurt"), true);
				cir.setReturnValue(false);
			}
		}
	}
}
