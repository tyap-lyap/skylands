package skylands.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

@SuppressWarnings("unused")
public class UseEntityEvent {

	public static ActionResult onUse(PlayerEntity player, World world, Hand hand, Entity entity) {
//		if(world.getRegistryKey().getValue().getNamespace().equals(Mod.MOD_ID)) {
//			var island = Skylands.instance.islandStuck.get(UUID.fromString(world.getRegistryKey().getValue().getPath()));
//			if(island.isPresent() && !island.get().isMember(player)) {
//				player.sendMessage(Text.of("Skylands > You can't interact with entities out here!"), true);
//				return ActionResult.FAIL;
//			}
//		}
		return ActionResult.PASS;
	}
}
