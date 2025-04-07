package skylands.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.server.network.ServerPlayerEntity;
import skylands.SkylandsMod;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class UpdateChecker {
	private static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();
	private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().build();
	private static Instant lastCheck = Instant.now().minus(4, ChronoUnit.HOURS);
	private static String cachedLatest = "0.1.0";
	private static ArrayList<ProjectVersion> cachedModrinth = new ArrayList<>();

	public static void onPlayerJoin(ServerPlayerEntity player) {
		if(Permissions.check(player, "skylands.update.checker.notify", 4)) {
			try {
				check(player);
			}
			catch (Exception e) {
				SkylandsMod.LOGGER.info("Failed to check for mod updates due to an exception: " + e);
			}
		}

	}

	static void check(ServerPlayerEntity player) {
		Version local = new Version(getLocalVersion());
		Version remote = new Version(getLatestRemote());

		if(local.compareTo(remote) < 0) {
			var text = SkylandsTexts.prefixed("message.skylands.new_update", map -> {
				map.put("%local_version%", local.version);
				map.put("%remote_version%", remote.version);
				map.put("%changes%", getChanges(local));
			});
			player.sendMessage(text);
		}
	}

	static String getChanges(Version local) {
		StringBuilder builder = new StringBuilder();

		for (ProjectVersion projectVersion : cachedModrinth) {
			Version version = new Version(projectVersion.versionNumber.split("\\+")[0]);
			if (version.compareTo(local) > 0) {
				builder.append(projectVersion.changelog.replaceAll("\\r\\n", "\n")
					.replaceAll("\\[(.*?)]\\(.*?\\)", "")
					.replaceAll(" \\(\\)", "")).append("\n");
			}
		}
		return builder.toString().trim();
	}

	static String getLatestRemote() {
		if(ChronoUnit.HOURS.between(lastCheck, Instant.now()) >= 4) {
			String remote = "0";
			var versions = getModrinthVersions();
			if(versions.isPresent() && !versions.get().isEmpty()) {
				remote = versions.get().get(0).versionNumber.split("\\+")[0];
			}
			cachedLatest = remote;
			lastCheck = Instant.now();
		}
		return cachedLatest;
	}

	static String getLocalVersion() {
		String version = "10.0";
		var mod = FabricLoader.getInstance().getModContainer("skylands");

		if(mod.isPresent()) {
			version = mod.get().getMetadata().getVersion().getFriendlyString().split("\\+")[0];
		}

		return version;
	}

	static Optional<ArrayList<ProjectVersion>> getModrinthVersions() {
		try {
			HttpRequest request = HttpRequest.newBuilder()
				.GET()
				.uri(new URI("https://api.modrinth.com/v2/project/skylands/version"))
				.headers("User-Agent", "tyap-lyap/skylands/" + getLocalVersion())
				.build();

			HttpResponse<InputStream> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream());
			InputStreamReader reader = new InputStreamReader(response.body(), StandardCharsets.UTF_8);
			ProjectVersion[] versions = GSON.fromJson(reader, ProjectVersion[].class);

			var array = new ArrayList<ProjectVersion>();

			Arrays.asList(versions).forEach(version -> {
				if(version.gameVersions.contains(SharedConstants.getGameVersion().getName())) array.add(version);
			});
			cachedModrinth = array;
			return Optional.of(array);
		}
		catch(FileNotFoundException e) {
			return Optional.empty();
		}
		catch(Exception e) {
			SkylandsMod.LOGGER.info("Failed to parse modrinth project versions due to an exception:\n" + e);
		}
		return Optional.empty();
	}

	@SuppressWarnings("unused")
	static class ProjectVersion {
		public String id = "";
		@SerializedName("project_id")
		public String projectId = "";
		@SerializedName("author_id")
		public String authorId = "";
		public boolean featured = false;
		public String name = "";
		@SerializedName("version_number")
		public String versionNumber = "";
		public String changelog = "";
		@SerializedName("changelog_url")
		public String changelogUrl = "";
		@SerializedName("date_published")
		public String published = "";
		public int downloads = 0;
		@SerializedName("version_type")
		public String versionType = "";
		@SerializedName("game_versions")
		public ArrayList<String> gameVersions = new ArrayList<>();
		public ArrayList<String> loaders = new ArrayList<>();
	}

	// Credit https://stackoverflow.com/questions/198431/how-do-you-compare-two-version-strings-in-java
	public static class Version implements Comparable<Version> {

		final String version;

		public Version(String version) {
			if(version == null)
				throw new IllegalArgumentException("Version can not be null");
			if(!version.matches("[0-9]+(\\.[0-9]+)*"))
				throw new IllegalArgumentException("Invalid version format");
			this.version = version;
		}

		@Override public int compareTo(Version that) {
			if(that == null)
				return 1;
			String[] thisParts = this.version.split("\\.");
			String[] thatParts = that.version.split("\\.");
			int length = Math.max(thisParts.length, thatParts.length);
			for(int i = 0; i < length; i++) {
				int thisPart = i < thisParts.length ?
					Integer.parseInt(thisParts[i]) : 0;
				int thatPart = i < thatParts.length ?
					Integer.parseInt(thatParts[i]) : 0;
				if(thisPart < thatPart)
					return -1;
				if(thisPart > thatPart)
					return 1;
			}
			return 0;
		}

		@Override public boolean equals(Object that) {
			if(this == that)
				return true;
			if(that == null)
				return false;
			if(this.getClass() != that.getClass())
				return false;
			return this.compareTo((Version) that) == 0;
		}

	}
}
