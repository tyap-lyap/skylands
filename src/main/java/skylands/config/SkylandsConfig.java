package skylands.config;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import skylands.SkylandsMod;
import skylands.config.template.HubTemplate;
import skylands.config.template.MainIslandTemplate;
import skylands.config.template.Metadata;
import skylands.config.template.Template;
import skylands.logic.Skylands;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SkylandsConfig {
	public static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting()
		.registerTypeAdapter(BlockPosition.class, new BlockPosition.JsonAdapter().nullSafe())
		.registerTypeAdapter(PlayerPosition.class, new PlayerPosition.JsonAdapter().nullSafe())
		.create();
	public static final SkylandsConfig DEFAULT = new SkylandsConfig();
	@SuppressWarnings("unused")
	public String readDocs = "https://github.com/tyap-lyap/skylands/wiki";

	public PlayerPosition defaultHubSpawnPos = new PlayerPosition(0.5D, 80.0D, 0.5D, 0, 0);

	public String rootCommand = "sl";
	public boolean hubProtectedByDefault = false;
	public int islandDeletionCooldown = (24 * 60) * 60;
	public boolean updateCheckerEnabled = true;
	public boolean teleportAfterIslandCreation = false;
	public boolean createIslandOnPlayerJoin = false;
	public boolean endDimensionIslandsEnabled = false;
	public boolean forceHubSpawnPos = false;
	public boolean hubTemplateEnabled = false;
	public HubTemplate hubTemplate = new HubTemplate("world", new Metadata("hub_template"));

	public ArrayList<MainIslandTemplate> islandTemplates = new ArrayList<>(List.of(new MainIslandTemplate("default", "structure",
		new Metadata("skylands:start_island", new BlockPosition(-7, 65, -7)),
		new PlayerPosition(0.5D, 75.0D, 0.5D),
		new PlayerPosition(0.5D, 75.0D, 0.5D),
		"default", "default")));

	public ArrayList<Template> netherTemplates = new ArrayList<>(List.of(new Template("default", "structure",
		new Metadata("skylands:nether_island", new BlockPosition(-7, 65, -7)))));

	public ArrayList<Template> endTemplates = new ArrayList<>(List.of(new Template("default", "structure",
		new Metadata("skylands:end_island", new BlockPosition(-7, 65, -7)))));

	public static void init() {
		Skylands.config = SkylandsConfig.read();
		Skylands.config.save();
	}

	public Optional<MainIslandTemplate> getIslandTemplate(String name) {
		for(MainIslandTemplate template : islandTemplates) {
			if(template.name.equals(name)) return Optional.of(template);
		}

		return Optional.empty();
	}

	public static SkylandsConfig read() {
		String filePath = FabricLoader.getInstance().getConfigDir().resolve("skylands.json").toString();
		try {
			BufferedReader fixReader = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8));
			var json = GSON.fromJson(fixReader, JsonObject.class);

			if (applyFixes(json)) {
				return GSON.fromJson(json, SkylandsConfig.class);
			}

			BufferedReader reader = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8));
			return GSON.fromJson(reader, SkylandsConfig.class);
		}
		catch(FileNotFoundException e) {
			SkylandsMod.LOGGER.info("File " + filePath + " is not found! Setting to default.");
			return new SkylandsConfig();
		}
		catch(Exception e) {
			SkylandsMod.LOGGER.info("Failed to read skylands config due to an exception. " +
					"Please delete skylands.json to regenerate config or fix the issue:\n" + e);
			e.printStackTrace();
			System.exit(0);
			return new SkylandsConfig();
		}
	}

	public void save() {
		try {
			String filePath = FabricLoader.getInstance().getConfigDir().resolve("skylands.json").toString();
			try(FileWriter writer = new FileWriter(filePath, StandardCharsets.UTF_8)) {
				writer.write(GSON.toJson(this));
			}
		}
		catch(Exception e) {
			SkylandsMod.LOGGER.info("Failed to save skylands config due to an exception:\n" + e);
		}
	}

	static boolean applyFixes(JsonObject json) {
		boolean fixed = false;

		if(json.has("defaultHubPos") && json.getAsJsonObject("defaultHubPos").has("field_1352")) {
			var defaultHubPos = json.getAsJsonObject("defaultHubPos");
			defaultHubPos.addProperty("x", defaultHubPos.getAsJsonPrimitive("field_1352").getAsDouble());
			defaultHubPos.addProperty("y", defaultHubPos.getAsJsonPrimitive("field_1351").getAsDouble());
			defaultHubPos.addProperty("z", defaultHubPos.getAsJsonPrimitive("field_1350").getAsDouble());
			fixed = true;
		}

		if(json.getAsJsonObject("defaultHubPos") != null) {
			json.add("defaultHubSpawnPos", json.getAsJsonObject("defaultHubPos"));
			json.remove("defaultHubPos");
			fixed = true;
		}

		if(json.has("defaultSpawnPos") && json.getAsJsonObject("defaultSpawnPos").has("field_1352")) {
			var defaultSpawnPos = json.getAsJsonObject("defaultSpawnPos");
			defaultSpawnPos.addProperty("x", defaultSpawnPos.getAsJsonPrimitive("field_1352").getAsDouble());
			defaultSpawnPos.addProperty("y", defaultSpawnPos.getAsJsonPrimitive("field_1351").getAsDouble());
			defaultSpawnPos.addProperty("z", defaultSpawnPos.getAsJsonPrimitive("field_1350").getAsDouble());
			fixed = true;
		}

		if(json.has("defaultVisitsPos") && json.getAsJsonObject("defaultVisitsPos").has("field_1352")) {
			var defaultVisitsPos = json.getAsJsonObject("defaultVisitsPos");
			defaultVisitsPos.addProperty("x", defaultVisitsPos.getAsJsonPrimitive("field_1352").getAsDouble());
			defaultVisitsPos.addProperty("y", defaultVisitsPos.getAsJsonPrimitive("field_1351").getAsDouble());
			defaultVisitsPos.addProperty("z", defaultVisitsPos.getAsJsonPrimitive("field_1350").getAsDouble());
			fixed = true;
		}

		if(json.has("defaultSpawnPos") && json.has("defaultVisitsPos")) {
			Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();

			var defaultSpawnPos = gson.fromJson(json.getAsJsonObject("defaultSpawnPos"), PlayerPosition.class);
			var defaultVisitsPos = gson.fromJson(json.getAsJsonObject("defaultVisitsPos"), PlayerPosition.class);

			ArrayList<MainIslandTemplate> islandTemplates = new ArrayList<>(List.of(new MainIslandTemplate("default", "structure",
				new Metadata("skylands:start_island", null), defaultSpawnPos, defaultVisitsPos,
				"default", "default")));

			json.add("islandTemplates", gson.toJsonTree(islandTemplates).getAsJsonArray());

			json.remove("defaultSpawnPos");
			json.remove("defaultVisitsPos");

			fixed = true;
		}

		return fixed;
	}

}
