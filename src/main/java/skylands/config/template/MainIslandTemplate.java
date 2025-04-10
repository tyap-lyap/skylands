package skylands.config.template;

import skylands.config.PlayerPosition;

public class MainIslandTemplate extends Template {
	public String netherTemplate;
	public String endTemplate;

	public MainIslandTemplate(String name, String type, Metadata metadata, PlayerPosition spawnPosition, PlayerPosition visitsPosition, String netherTemplate, String endTemplate) {
		super(name, type, metadata, spawnPosition, visitsPosition);
		this.netherTemplate = netherTemplate;
		this.endTemplate = endTemplate;
	}

}
