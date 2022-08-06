package skylands.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import skylands.Mod;
import skylands.logic.Skylands;

import java.util.UUID;

@Mixin(ItemFrameEntity.class)
public abstract class ItemFrameEntityMixin extends AbstractDecorationEntity {

	protected ItemFrameEntityMixin(EntityType<? extends AbstractDecorationEntity> entityType, World world) {
		super(entityType, world);
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

	@Inject(method = "interact", at = @At("HEAD"), cancellable = true)
	void interact(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		if(!player.world.isClient && player.world.getRegistryKey().getValue().getNamespace().equals(Mod.MOD_ID)) {
			var island = Skylands.instance.islandStuck.get(UUID.fromString(player.world.getRegistryKey().getValue().getPath()));
			if(island.isPresent() && !island.get().isMember(player)) {
				player.sendMessage(Text.of("Skylands > You can't interact with entities out here!"), true);
				cir.setReturnValue(ActionResult.FAIL);
			}
		}
	}
}
