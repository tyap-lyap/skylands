package skylands.event;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import org.apache.commons.io.FileUtils;
import skylands.SkylandsMod;
import skylands.logic.Skylands;

import java.io.File;

public class ServerStartEvent {

	public static void onStarting(MinecraftServer server) {
		Skylands.instance = new Skylands(server);

		try {
			File hubTemplate = server.getFile("hub_template");
			String path = server.getSavePath(WorldSavePath.DATAPACKS).toFile().toString().replace("\\datapacks", "");
			File lock = new File(path + "\\copied.lock");

			if(hubTemplate.exists() && !lock.exists()) {
				FileUtils.copyDirectory(hubTemplate, new File(path));
				lock.createNewFile();
			}
		}
		catch (Exception e) {
			SkylandsMod.LOGGER.error("Failed to copy hub template due to an exception: " + e);
			e.printStackTrace();
		}

		var motd = server.getServerMotd();

		if(motd == null || motd.equals("A Minecraft Server")) {
			FabricLoader.getInstance().getModContainer("skylands").ifPresent(mod -> {
				var modMeta = mod.getMetadata();
				server.setMotd("Skylands Beta v" + modMeta.getVersion().getFriendlyString());
			});
		}
	}
}
