package skylands.data;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import skylands.SkylandsMod;

public class Components implements WorldComponentInitializer, EntityComponentInitializer {
	public static final ComponentKey<WorldComponent> WORLD_DATA = ComponentRegistryV3.INSTANCE.getOrCreate(SkylandsMod.id("world_data"), WorldComponent.class);
	public static final ComponentKey<PlayerComponent> PLAYER_DATA = ComponentRegistryV3.INSTANCE.getOrCreate(SkylandsMod.id("player_data"), PlayerComponent.class);

	@Override
	public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
		registry.register(WORLD_DATA, WorldComponent::new);
	}

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		registry.registerForPlayers(PLAYER_DATA, PlayerComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
	}
}
