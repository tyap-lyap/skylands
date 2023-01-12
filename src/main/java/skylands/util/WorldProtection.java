package skylands.util;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import skylands.logic.Skylands;

public class WorldProtection {

	public static boolean canModify(World world, PlayerEntity player) {
		var island = Worlds.getIsland(world);

		if(Permissions.check(player, "skylands.world.protection.bypass", false)) {
			return true;
		}

		if(island.isPresent() && !island.get().isMember(player)) {
			return false;
		}

		if(world.getRegistryKey().equals(World.OVERWORLD)) {
			return !Skylands.instance.hub.hasProtection;
		}
		return true;
	}
}
