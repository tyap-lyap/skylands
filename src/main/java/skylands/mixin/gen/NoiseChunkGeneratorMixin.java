package skylands.mixin.gen;

import net.minecraft.structure.StructureSet;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import skylands.util.Worlds;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

@Mixin(NoiseChunkGenerator.class)
public abstract class NoiseChunkGeneratorMixin extends ChunkGenerator {

	public NoiseChunkGeneratorMixin(Registry<StructureSet> structureSetRegistry, Optional<RegistryEntryList<StructureSet>> structureOverrides, BiomeSource biomeSource) {
		super(structureSetRegistry, structureOverrides, biomeSource);
	}

	public NoiseChunkGeneratorMixin(Registry<StructureSet> structureSetRegistry, Optional<RegistryEntryList<StructureSet>> structureOverrides, BiomeSource biomeSource, Function<RegistryEntry<Biome>, GenerationSettings> generationSettingsGetter) {
		super(structureSetRegistry, structureOverrides, biomeSource, generationSettingsGetter);
	}

	@Override
	public void generateFeatures(StructureWorldAccess structureWorldAccess, Chunk chunk, StructureAccessor structureAccessor) {
		if (structureWorldAccess instanceof World world) {
			if (!Worlds.isIsland(world)) {
				super.generateFeatures(structureWorldAccess, chunk, structureAccessor);
			}
		}
		if (structureWorldAccess instanceof ChunkRegion chunkRegion) {
			if (!Worlds.isIsland(chunkRegion.toServerWorld())) {
				super.generateFeatures(structureWorldAccess, chunk, structureAccessor);
			}
		}
	}

	@Shadow
	public abstract CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk);

	@Inject(method = "populateNoise(Ljava/util/concurrent/Executor;Lnet/minecraft/world/gen/chunk/Blender;Lnet/minecraft/world/gen/noise/NoiseConfig;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/chunk/Chunk;)Ljava/util/concurrent/CompletableFuture;", at = @At("HEAD"), cancellable = true)
	public void onPopulateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk, CallbackInfoReturnable<CompletableFuture<Chunk>> cir) {
		WorldAccess worldAccess = ((StructureAccessorAccessor) structureAccessor).getWorld();
		if (worldAccess instanceof World world) {
			if (Worlds.isIsland(world)) {
				cir.setReturnValue(CompletableFuture.completedFuture(chunk));
			}
		}
		if (worldAccess instanceof ChunkRegion chunkRegion) {
			if (Worlds.isIsland(chunkRegion.toServerWorld())) {
				cir.setReturnValue(CompletableFuture.completedFuture(chunk));
			}
		}
	}
}
