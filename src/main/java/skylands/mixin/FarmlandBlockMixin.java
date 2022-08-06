package skylands.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import skylands.Mod;
import skylands.logic.Skylands;

import java.util.UUID;

@Mixin(FarmlandBlock.class)
public abstract class FarmlandBlockMixin {

	@Inject(method = "onLandedUpon", at = @At("HEAD"), cancellable = true)
	void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci) {
		if(!world.isClient && entity instanceof PlayerEntity player) {
			if(world.getRegistryKey().getValue().getNamespace().equals(Mod.MOD_ID)) {
				var island = Skylands.instance.islandStuck.get(UUID.fromString(world.getRegistryKey().getValue().getPath()));
				if(island.isPresent() && !island.get().isMember(player)) {
					player.sendMessage(Text.of("Skylands > You can't spoil farmlands out here!"), true);
					ci.cancel();
				}
			}
		}
	}
}
