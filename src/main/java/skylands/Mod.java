package skylands;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import skylands.command.ModCommands;
import skylands.data.reloadable.SongsData;
import skylands.event.ModEvents;

public class Mod implements ModInitializer {
	public static final String MOD_ID = "skylands";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModEvents.init();
		ModCommands.init();
		SongsData.init();
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}
