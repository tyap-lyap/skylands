package skylands.config.template;

import skylands.config.PlayerPosition;

public class IslandTemplate extends Template {
	public String netherTemplate;

	public IslandTemplate(String name, String type, Metadata metadata, PlayerPosition playerSpawnPosition, String netherTemplate) {
		super(name, type, metadata, playerSpawnPosition);
		this.netherTemplate = netherTemplate;
	}

}
