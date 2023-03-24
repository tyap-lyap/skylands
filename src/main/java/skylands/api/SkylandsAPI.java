package skylands.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import skylands.SkylandsMod;
import skylands.logic.Island;
import skylands.logic.Skylands;

import java.util.Optional;
import java.util.UUID;

public class SkylandsAPI {
	static Skylands skylands = Skylands.getInstance();

	public static final Event<HubVisitEvent> ON_HUB_VISIT = EventFactory.createArrayBacked(HubVisitEvent.class, callbacks -> (player, world) -> {
		for (HubVisitEvent callback : callbacks) {
			callback.invoke(player, world);
		}
	});
	@FunctionalInterface public interface HubVisitEvent {void invoke(PlayerEntity player, World world);}

	public static final Event<GenericIslandEvent> ON_ISLAND_VISIT = EventFactory.createArrayBacked(GenericIslandEvent.class, callbacks -> (player, world, island) -> {
		for (GenericIslandEvent callback : callbacks) {
			callback.invoke(player, world, island);
		}
	});

	public static final Event<GenericIslandEvent> ON_ISLAND_FIRST_LOAD = EventFactory.createArrayBacked(GenericIslandEvent.class, callbacks -> (player, world, island) -> {
		for (GenericIslandEvent callback : callbacks) {
			callback.invoke(player, world, island);
		}
	});
	@FunctionalInterface public interface GenericIslandEvent {void invoke(PlayerEntity player, World world, Island island);}

	public static final Event<NetherFirstLoad> ON_NETHER_FIRST_LOAD = EventFactory.createArrayBacked(NetherFirstLoad.class, callbacks -> (world, island) -> {
		for (NetherFirstLoad callback : callbacks) {
			callback.onLoad(world, island);
		}
	});
	@FunctionalInterface public interface NetherFirstLoad {void onLoad(World world, Island island);}

	public static Optional<Island> getIsland(PlayerEntity player) {
		return skylands.islands.get(player);
	}

	public static Optional<Island> getIsland(String playerName) {
		return skylands.islands.get(playerName);
	}

	public static Optional<Island> getIsland(UUID playerUuid) {
		return skylands.islands.get(playerUuid);
	}

	public static boolean isIsland(World world) {
		return isIsland(world.getRegistryKey());
	}

	public static boolean isIsland(RegistryKey<World> registryKey) {
		var namespace = registryKey.getValue().getNamespace();
		return namespace.equals(SkylandsMod.MOD_ID) || namespace.equals("nether") || namespace.equals("end");
	}

	public static Optional<Island> getIsland(World world) {
		if (isIsland(world)) {
			try {
				return Skylands.instance.islands.get(UUID.fromString(world.getRegistryKey().getValue().getPath()));
			}
			catch (Exception e) {
				return Optional.empty();
			}
		}
		return Optional.empty();
	}

}
