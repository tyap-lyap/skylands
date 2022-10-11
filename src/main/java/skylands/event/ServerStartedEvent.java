package skylands.event;

import net.minecraft.server.MinecraftServer;
import skylands.SkylandsMod;
import skylands.logic.Island;
import skylands.logic.Skylands;

public class ServerStartedEvent {
	public static void onStarted(MinecraftServer server) {
		for (Island island : Skylands.instance.islands.stuck) {
			SkylandsMod.LOGGER.info("Loading " + island.owner.name + "'s Island...");
			island.getWorld();
			if (island.hasNether) {
				SkylandsMod.LOGGER.info("Loading " + island.owner.name + "'s Nether...");
				island.getNether();
			}
		}
		if (Skylands.instance.islands.stuck.size() > 0) {
			SkylandsMod.LOGGER.info("All islands has been loaded!");
		}
	}
}
