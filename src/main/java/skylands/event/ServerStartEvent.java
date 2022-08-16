package skylands.event;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import skylands.logic.Skylands;

public class ServerStartEvent {

	public static void onStart(MinecraftServer server) {
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
}
