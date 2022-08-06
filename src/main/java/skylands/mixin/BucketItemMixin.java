package skylands.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import skylands.Mod;
import skylands.logic.Skylands;

import java.util.UUID;

@Mixin(BucketItem.class)
public abstract class BucketItemMixin {

	@Inject(method = "use", at = @At("HEAD"), cancellable = true)
	void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
		if(!world.isClient) {
			if(world.getRegistryKey().getValue().getNamespace().equals(Mod.MOD_ID)) {
				var island = Skylands.instance.islandStuck.get(UUID.fromString(world.getRegistryKey().getValue().getPath()));
				if(island.isPresent() && !island.get().isMember(user)) {
					user.sendMessage(Text.of("Skylands > You can't take fluids out here!"), true);
					cir.setReturnValue(TypedActionResult.fail(user.getStackInHand(hand)));
				}
			}
		}
	}
}
