package skylands;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import skylands.command.Commands;
import skylands.event.Events;

public class Mod implements ModInitializer {
	public static final String MOD_ID = "skylands";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		Events.init();
		Commands.init();
		//Example code, you can remove it
		FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(mod -> {
			ModMetadata meta = mod.getMetadata();
			LOGGER.info(meta.getName() + " " + meta.getVersion().getFriendlyString() + " successfully initialized!");
		});
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}
