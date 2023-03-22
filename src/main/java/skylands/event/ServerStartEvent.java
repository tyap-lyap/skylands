package skylands.event;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import skylands.logic.Skylands;

public class ServerStartEvent {

	public static void onStart(MinecraftServer server) {
		Skylands.instance = new Skylands(server);

		var motd = server.getServerMotd();

		if(motd == null || motd.equals("A Minecraft Server")) {
			FabricLoader.getInstance().getModContainer("skylands").ifPresent(mod -> {
				var modMeta = mod.getMetadata();
				server.setMotd("Skylands Beta v" + modMeta.getVersion().getFriendlyString());
			});
		}
	}
}
