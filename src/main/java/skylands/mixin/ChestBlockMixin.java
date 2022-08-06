package skylands.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import skylands.Mod;
import skylands.logic.Skylands;

import java.util.UUID;

@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin {

	@Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
	void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
		if(!world.isClient) {
			if(world.getRegistryKey().getValue().getNamespace().equals(Mod.MOD_ID)) {
				var island = Skylands.instance.islandStuck.get(UUID.fromString(world.getRegistryKey().getValue().getPath()));
				if(island.isPresent() && !island.get().isMember(player)) {
					player.sendMessage(Text.of("Skylands > You can't open chests out here!"), true);
					cir.setReturnValue(ActionResult.FAIL);
				}
			}
		}
	}
}
