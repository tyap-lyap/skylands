package skylands.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.JsonAdapter;
import net.fabricmc.loader.api.FabricLoader;
import skylands.SkylandsMod;
import skylands.logic.Skylands;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class SkylandsConfig {
	public static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();
	@SuppressWarnings("unused")
	public String readDocs = "https://github.com/tyap-lyap/skylands/wiki";
	@JsonAdapter(PlayerPosition.JsonAdapter.class)
	public PlayerPosition defaultSpawnPos = new PlayerPosition(0.5D, 75.0D, 0.5D, 0, 0);
	@JsonAdapter(PlayerPosition.JsonAdapter.class)
	public PlayerPosition defaultVisitsPos = new PlayerPosition(0.5D, 75.0D, 0.5D, 0, 0);
	@JsonAdapter(PlayerPosition.JsonAdapter.class)
	public PlayerPosition defaultHubPos = new PlayerPosition(0.5D, 80.0D, 0.5D, 0, 0);
	public boolean hubProtectedByDefault = false;
	public int islandDeletionCooldown = (24 * 60) * 60;

	public boolean updateCheckerEnabled = true;
	public boolean teleportAfterIslandCreation = false;
	public boolean createIslandOnPlayerJoin = false;
	public boolean forceHubSpawnPos = false;
	public boolean hubTemplateEnabled = false;
	public HubTemplate hubTemplate = new HubTemplate("world", new Metadata("hub_template"));

	public ArrayList<IslandTemplate> islandTemplates = new ArrayList<>(List.of(new IslandTemplate("default", "structure",
		new Metadata("skylands:start_island", new BlockPosition(-7, 65, -7)), defaultSpawnPos, "default")));

	public ArrayList<Template> netherTemplates = new ArrayList<>(List.of(new Template("default", "structure",
		new Metadata("skylands:nether_island", new BlockPosition(-7, 65, -7)), defaultSpawnPos)));

	public static void init() {
		Skylands.config = SkylandsConfig.read();
	}

	public static SkylandsConfig read() {
		String filePath = FabricLoader.getInstance().getConfigDir().resolve("skylands.json").toString();
		try {
			BufferedReader fixReader = new BufferedReader(new FileReader(filePath));
			var json = GSON.fromJson(fixReader, JsonObject.class);
			boolean fixed = false;

			if(json.getAsJsonObject("defaultSpawnPos").has("field_1352")) {
				var defaultSpawnPos = json.getAsJsonObject("defaultSpawnPos");
				defaultSpawnPos.addProperty("x", defaultSpawnPos.getAsJsonPrimitive("field_1352").getAsDouble());
				defaultSpawnPos.addProperty("y", defaultSpawnPos.getAsJsonPrimitive("field_1351").getAsDouble());
				defaultSpawnPos.addProperty("z", defaultSpawnPos.getAsJsonPrimitive("field_1350").getAsDouble());
				fixed = true;
			}

			if(json.getAsJsonObject("defaultVisitsPos").has("field_1352")) {
				var defaultVisitsPos = json.getAsJsonObject("defaultVisitsPos");
				defaultVisitsPos.addProperty("x", defaultVisitsPos.getAsJsonPrimitive("field_1352").getAsDouble());
				defaultVisitsPos.addProperty("y", defaultVisitsPos.getAsJsonPrimitive("field_1351").getAsDouble());
				defaultVisitsPos.addProperty("z", defaultVisitsPos.getAsJsonPrimitive("field_1350").getAsDouble());
				fixed = true;
			}

			if(json.getAsJsonObject("defaultHubPos").has("field_1352")) {
				var defaultHubPos = json.getAsJsonObject("defaultHubPos");
				defaultHubPos.addProperty("x", defaultHubPos.getAsJsonPrimitive("field_1352").getAsDouble());
				defaultHubPos.addProperty("y", defaultHubPos.getAsJsonPrimitive("field_1351").getAsDouble());
				defaultHubPos.addProperty("z", defaultHubPos.getAsJsonPrimitive("field_1350").getAsDouble());
				fixed = true;
			}

			if (fixed) {
				var fixedConfig = GSON.fromJson(json, SkylandsConfig.class);
				fixedConfig.save();
				return fixedConfig;
			}

			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			var config = GSON.fromJson(reader, SkylandsConfig.class);
			config.save();
			return config;
		}
		catch(FileNotFoundException e) {
			SkylandsMod.LOGGER.info("File " + filePath + " is not found! Setting to default.");
			var conf = new SkylandsConfig();
			conf.save();
			return conf;
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
			try(FileWriter writer = new FileWriter(filePath)) {
				writer.write(GSON.toJson(this));
			}
		}
		catch(Exception e) {
			SkylandsMod.LOGGER.info("Failed to save skylands config due to an exception:\n" + e);
		}
	}

}
