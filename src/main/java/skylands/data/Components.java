package skylands.data;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import skylands.Mod;

public class Components implements WorldComponentInitializer {
	public static final ComponentKey<WorldDataComponent> WORLD_DATA = ComponentRegistryV3.INSTANCE.getOrCreate(Mod.id("world_data"), WorldDataComponent.class);

	@Override
	public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
		registry.register(WORLD_DATA, WorldDataComponent::new);
	}
}
