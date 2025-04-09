package skylands.config.template;

import skylands.config.PlayerPosition;

public class IslandTemplate extends Template {
	public String netherTemplate;
	public String endTemplate;

	public IslandTemplate(String name, String type, Metadata metadata, PlayerPosition playerSpawnPosition, String netherTemplate, String endTemplate) {
		super(name, type, metadata, playerSpawnPosition);
		this.netherTemplate = netherTemplate;
		this.endTemplate = endTemplate;
	}

}
