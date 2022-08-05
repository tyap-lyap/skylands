package skylands.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import skylands.logic.Skylands;

import java.util.UUID;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

	public LivingEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(method = "damage", at = @At("HEAD"), cancellable = true)
	void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		LivingEntity self = LivingEntity.class.cast(this);

		if(!world.isClient && world.getRegistryKey().getValue().getNamespace().equals("skylands")) {
			var island = Skylands.instance.islandStuck.get(UUID.fromString(world.getRegistryKey().getValue().getPath()));
			if(island.isPresent()) {
				if(self instanceof PlayerEntity player) {
					if(!island.get().isMember(player)) {
						player.sendMessage(Text.of("Skylands > You can't take damage at someone's island"), true);
						cir.setReturnValue(false);
					}
				}
				if(source.getAttacker() instanceof PlayerEntity attacker) {
					if(!island.get().isMember(attacker)) {
						attacker.sendMessage(Text.of("Skylands > You can't damage entities at someone's island!"));
						cir.setReturnValue(false);
					}
				}
			}

		}
	}
}
