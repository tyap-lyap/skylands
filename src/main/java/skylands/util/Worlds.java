package skylands.util;

import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import skylands.SkylandsMod;
import skylands.logic.Island;
import skylands.logic.Skylands;

import java.util.Optional;
import java.util.UUID;

public class Worlds {

	public static boolean isIsland(World world) {
		return isIsland(world.getRegistryKey());
	}

	public static boolean isIsland(RegistryKey<World> registryKey) {
		var namespace = registryKey.getValue().getNamespace();
		return namespace.equals(SkylandsMod.MOD_ID) || namespace.equals("nether") || namespace.equals("end");
	}

	public static boolean isOverworld(RegistryKey<World> registryKey) {
		var namespace = registryKey.getValue().getNamespace();
		return namespace.equals(SkylandsMod.MOD_ID) || registryKey == World.OVERWORLD;
	}

	public static boolean isNether(RegistryKey<World> registryKey) {
		var namespace = registryKey.getValue().getNamespace();
		return namespace.equals("nether") || registryKey == World.NETHER;
	}

	public static boolean isEnd(RegistryKey<World> registryKey) {
		var namespace = registryKey.getValue().getNamespace();
		return namespace.equals("end") || registryKey == World.END;
	}

	public static RegistryKey<World> redirect(RegistryKey<World> registryKey) {
		if (isOverworld(registryKey)) {
			return World.OVERWORLD;
		}
		if (isEnd(registryKey)) {
			return World.END;
		}
		if (isNether(registryKey)) {
			return World.NETHER;
		}
		return registryKey;
	}

	public static Optional<Island> getIsland(World world) {
		if (isIsland(world)) {
			return Skylands.instance.islands.get(UUID.fromString(world.getRegistryKey().getValue().getPath()));
		}
		return Optional.empty();
	}

}
