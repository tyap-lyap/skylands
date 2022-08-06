package skylands.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import skylands.logic.Skylands;

import java.util.UUID;

@Mixin(AbstractDecorationEntity.class)
public abstract class DecorationEntityMixin extends Entity {

	public DecorationEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(method = "damage", at = @At("HEAD"), cancellable = true)
	void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		if(!world.isClient && world.getRegistryKey().getValue().getNamespace().equals("skylands")) {
			var island = Skylands.instance.islandStuck.get(UUID.fromString(world.getRegistryKey().getValue().getPath()));
			if(island.isPresent()) {
				if(source.getAttacker() instanceof PlayerEntity attacker) {
					if(!island.get().isMember(attacker)) {
						attacker.sendMessage(Text.of("Skylands > You can't damage entities at someone's island!"), true);
						cir.setReturnValue(false);
					}
				}
			}
		}
	}
}
