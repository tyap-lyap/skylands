package skylands.data;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import skylands.Mod;

public class Components implements WorldComponentInitializer {
	public static final ComponentKey<WorldComponent> WORLD_DATA = ComponentRegistryV3.INSTANCE.getOrCreate(Mod.id("world_data"), WorldComponent.class);

	@Override
	public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
		registry.register(WORLD_DATA, WorldComponent::new);
	}
}
