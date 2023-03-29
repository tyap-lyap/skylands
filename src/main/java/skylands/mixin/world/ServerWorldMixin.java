package skylands.mixin.world;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerEntityManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
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
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import skylands.SkylandsMod;
import skylands.logic.Island;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements StructureWorldAccess {

	@Mutable @Shadow @Final private ServerChunkManager chunkManager;
	@Shadow @Final private ServerEntityManager<Entity> entityManager;
	@Mutable @Shadow @Final private List<Spawner> spawners;

	@Shadow
	@NotNull
	public abstract MinecraftServer getServer();

	protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, RegistryEntry<DimensionType> dimension, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
		super(properties, registryRef, dimension, profiler, isClient, debugWorld, seed, maxChainedNeighborUpdates);
	}


	@Inject(method = "<init>", at = @At("TAIL"))
	void init(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey<World> worldKey, DimensionOptions dimensionOptions, WorldGenerationProgressListener worldGenerationProgressListener, boolean debugWorld, long seed, List<Spawner> spawners, boolean shouldTickTime, CallbackInfo ci) {

		if(worldKey.getValue().getNamespace().equals(SkylandsMod.MOD_ID)) {
			this.spawners = ImmutableList.of(new PhantomSpawner(), new PatrolSpawner(), new CatSpawner(), new ZombieSiegeManager(), new WanderingTraderManager(properties));
		}

		if(worldKey.equals(World.OVERWORLD) && server.getFile("hub_template").exists()) {
			FlatChunkGeneratorConfig flat = new FlatChunkGeneratorConfig(Optional.empty(), BuiltinRegistries.BIOME);
			flat.setBiome(getServer().getRegistryManager().get(Registry.BIOME_KEY).getOrCreateEntry(BiomeKeys.PLAINS));
			FlatChunkGenerator generator = new FlatChunkGenerator(Island.EMPTY_STRUCTURE_REGISTRY, flat);

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
		}

	}
}
