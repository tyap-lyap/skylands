package skylands;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import skylands.command.SkylandsCommands;
import skylands.data.reloadable.SongsData;
import skylands.event.SkylandsEvents;

public class SkylandsMod implements ModInitializer {
	public static final String MOD_ID = "skylands";
	public static final Logger LOGGER = LoggerFactory.getLogger("Skylands");

	@Override
	public void onInitialize() {
		SkylandsEvents.init();
		SkylandsCommands.init();
		SongsData.init();
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}
