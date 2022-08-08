package skylands.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import skylands.Mod;
import skylands.logic.Skylands;

import java.util.UUID;

public class WorldProtection {

	public static boolean canModify(World world, PlayerEntity player) {
		if(world.getRegistryKey().getValue().getNamespace().equals(Mod.MOD_ID)) {
			var uuid = UUID.fromString(world.getRegistryKey().getValue().getPath());
			var island = Skylands.instance.islandStuck.get(uuid);
			if(island.isPresent() && !island.get().isMember(player)) {
				return false;
			}
		}
		if(world.getRegistryKey().equals(World.OVERWORLD)) {
			return !Skylands.instance.hub.hasProtection;
		}
		return true;
	}
}
