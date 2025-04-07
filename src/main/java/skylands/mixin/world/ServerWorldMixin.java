package skylands.mixin.world;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.Entity;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerEntityManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.random.RandomSequencesState;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.village.ZombieSiegeManager;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WanderingTraderManager;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.spawner.CatSpawner;
import net.minecraft.world.spawner.PatrolSpawner;
import net.minecraft.world.spawner.PhantomSpawner;
import net.minecraft.world.spawner.Spawner;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import skylands.SkylandsMod;
import skylands.logic.Skylands;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements StructureWorldAccess {

	@Mutable @Shadow @Final private ServerChunkManager chunkManager;
	@Shadow @Final private ServerEntityManager<Entity> entityManager;
	@Mutable @Shadow @Final private List<Spawner> spawners;

	protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
		super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	void init(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey<World> worldKey, DimensionOptions dimensionOptions, WorldGenerationProgressListener worldGenerationProgressListener, boolean debugWorld, long seed, List<Spawner> spawners, boolean shouldTickTime, RandomSequencesState randomSequencesState, CallbackInfo ci) {

		if(worldKey.getValue().getNamespace().equals(SkylandsMod.MOD_ID)) {
			this.spawners = ImmutableList.of(new PhantomSpawner(), new PatrolSpawner(), new CatSpawner(), new ZombieSiegeManager(), new WanderingTraderManager(properties));
		}

		String path = server.getSavePath(WorldSavePath.DATAPACKS).toFile().toString().replace("\\datapacks", "");

		if(worldKey.equals(World.OVERWORLD) && Skylands.config.hubTemplateEnabled && (!new File(path + "\\region").exists() || new File(path + "\\copied.lock").exists())) {
			var biome = server.getRegistryManager().get(RegistryKeys.BIOME).getEntry(server.getRegistryManager().get(RegistryKeys.BIOME).getOrThrow(BiomeKeys.PLAINS));
			FlatChunkGeneratorConfig flat = new FlatChunkGeneratorConfig(Optional.of(RegistryEntryList.of()), biome, List.of());
			FlatChunkGenerator generator = new FlatChunkGenerator(flat);

			chunkManager = new ServerChunkManager(
				this.toServerWorld(),
				session,
				server.getDataFixer(),
				server.getStructureTemplateManager(),
				workerExecutor,
				generator,
				server.getPlayerManager().getViewDistance(),
				server.getPlayerManager().getSimulationDistance(),
				server.syncChunkWrites(),
				worldGenerationProgressListener,
				entityManager::updateTrackingStatus,
				() -> server.getOverworld().getPersistentStateManager()
			);

			SkylandsMod.LOGGER.info("Overworld generator got overwritten with empty world");
		}

	}
}
