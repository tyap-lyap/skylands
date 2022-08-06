package skylands.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import skylands.Mod;
import skylands.logic.Skylands;

import java.util.UUID;

@Mixin(StorageMinecartEntity.class)
public abstract class StorageMinecartMixin {

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
