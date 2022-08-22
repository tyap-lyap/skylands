package skylands.data.reloadable;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import nota.model.Playlist;
import nota.utils.NBSDecoder;
import skylands.Mod;

import java.io.InputStream;

public class SongsData implements SimpleSynchronousResourceReloadListener {
	public static final SongsData INSTANCE = new SongsData();

	public Playlist playlist = null;

	public static void init() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(SongsData.INSTANCE);
	}

	@Override
	public Identifier getFabricId() {
		return Mod.id("songs");
	}

	@Override
	public void reload(ResourceManager manager) {
		if(playlist == null) {
			for(Identifier id : manager.findResources("songs", path -> path.getPath().endsWith(".nbs")).keySet()) {
				try(InputStream stream = manager.getResource(id).get().getInputStream()) {
					if(playlist == null) {
						playlist = new Playlist(NBSDecoder.parse(stream));
					}
					else {
						playlist.add(NBSDecoder.parse(stream));
					}
				}
				catch(Exception e) {
					Mod.LOGGER.error("Error occurred while loading resource nbs " + id.toString(), e);
				}
			}
		}

	}
}
