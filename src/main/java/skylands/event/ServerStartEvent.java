package skylands.event;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import skylands.SkylandsMod;
import skylands.logic.Island;
import skylands.logic.Skylands;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class ServerStartEvent {

	public static void onStarting(MinecraftServer server) {
		Skylands.instance = new Skylands(server);

		var serverMeta = server.getServerMetadata();
		var motd = serverMeta.getDescription();

		if(motd == null || motd.getString().equals("A Minecraft Server")) {
			FabricLoader.getInstance().getModContainer("skylands").ifPresent(mod -> {
				var modMeta = mod.getMetadata();
				serverMeta.setDescription(Text.of("Skylands Alpha Build " + modMeta.getVersion().getFriendlyString()));
			});
		}
	}

	public static void onStarted(MinecraftServer server) {
		Instant inst = Instant.now();
		for (Island island : Skylands.instance.islands.stuck) {
			SkylandsMod.LOGGER.info("Loading " + island.owner.name + "'s Island...");
			island.getWorld();
			if (island.hasNether) {
				SkylandsMod.LOGGER.info("Loading " + island.owner.name + "'s Nether...");
				island.getNether();
			}
		}
		if (Skylands.instance.islands.stuck.size() > 0) {
			SkylandsMod.LOGGER.info("All Islands got successfully loaded in " + ChronoUnit.MILLIS.between(inst, Instant.now()) + " ms!");
		}
	}
}
