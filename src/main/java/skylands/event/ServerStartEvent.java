package skylands.event;

import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.SaveProperties;
import org.apache.commons.io.FileUtils;
import skylands.SkylandsMod;
import skylands.logic.Skylands;

import java.io.File;
import java.util.Collection;

public class ServerStartEvent implements ServerLifecycleEvents.ServerStarting, ServerLifecycleEvents.ServerStarted {
	static final ServerStartEvent INSTANCE = new ServerStartEvent();
	boolean generateStructure = false;

	@Override
	public void onServerStarting(MinecraftServer server) {
		Skylands.instance = new Skylands(server);

		try {
			if(Skylands.config.hubTemplateEnabled) {
				var template = Skylands.config.hubTemplate;
				String path = server.getSavePath(WorldSavePath.DATAPACKS).toFile().toString().replace("\\datapacks", "");

				generateStructure = !new File(path + "\\region").exists();
				if(template.type.equals("world")) {
					File hubTemplate = server.getFile(template.metadata.path);

					if(!new File(path + "\\region").exists()) {
						File lock = new File(path + "\\copied.lock");

						if(hubTemplate.exists() && !lock.exists()) {
							FileUtils.copyDirectory(hubTemplate, new File(path));
							lock.createNewFile();
							SkylandsMod.LOGGER.info("Reloading datapacks from hub template...");
							reloadDatapacks(server);
						}
					}
				}
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

	@Override
	public void onServerStarted(MinecraftServer server) {
		try {
			if(Skylands.config.hubTemplateEnabled && Skylands.config.hubTemplate.type.equals("structure")) {
				var template = Skylands.config.hubTemplate;
				String path = server.getSavePath(WorldSavePath.DATAPACKS).toFile().toString().replace("\\datapacks", "");
				File lock = new File(path + "\\copied.lock");

				if(!lock.exists() && generateStructure) {
					template.generateStructure(server.getOverworld());
					lock.createNewFile();
					generateStructure = false;
				}

			}
		}
		catch (Exception e) {
			SkylandsMod.LOGGER.error("Failed to generate hub template due to an exception: " + e);
			e.printStackTrace();
		}

	}

	private static void reloadDatapacks(MinecraftServer server) {
		ResourcePackManager resourcePackManager = server.getDataPackManager();
		SaveProperties saveProperties = server.getSaveProperties();
		Collection<String> collection = resourcePackManager.getEnabledNames();
		Collection<String> dataPacks = findNewDataPacks(resourcePackManager, saveProperties, collection);
		server.reloadResources(dataPacks).exceptionally(throwable -> null);
	}

	private static Collection<String> findNewDataPacks(ResourcePackManager dataPackManager, SaveProperties saveProperties, Collection<String> enabledDataPacks) {
		dataPackManager.scanPacks();
		Collection<String> collection = Lists.newArrayList(enabledDataPacks);
		Collection<String> collection2 = saveProperties.getDataConfiguration().dataPacks().getDisabled();

		for(String string : dataPackManager.getNames()) {
			if (!collection2.contains(string) && !collection.contains(string)) {
				collection.add(string);
			}
		}

		return collection;
	}
}
