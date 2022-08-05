package skylands.logic;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.Difficulty;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import org.apache.commons.lang3.RandomStringUtils;
import skylands.Mod;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.fantasy.RuntimeWorldHandle;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class Island {
	public UUID ownerUUID;
	public ArrayList<UUID> members = new ArrayList<>();
	private RuntimeWorldHandle handler = null;

	public Vec3d spawnPos = new Vec3d(0.5D, 75D, 0.5D);

	public Island(UUID uuid) {
		this.ownerUUID = uuid;
	}

	private RuntimeWorldHandle createWorld() {
		FlatChunkGeneratorConfig flat = new FlatChunkGeneratorConfig(Optional.empty(), BuiltinRegistries.BIOME);
		var generator = new FlatChunkGenerator(BuiltinRegistries.STRUCTURE_SET, flat);

		RuntimeWorldConfig config = new RuntimeWorldConfig()
				.setDimensionType(DimensionTypes.OVERWORLD)
				.setGenerator(generator)
				.setDifficulty(Difficulty.HARD)
				.setSeed(Long.parseLong(RandomStringUtils.randomNumeric(9)));

		return Skylands.instance.fantasy.getOrOpenPersistentWorld(Mod.id(this.ownerUUID.toString()), config);
	}

	public ServerWorld getWorld() {
		if(this.handler == null) {
			RuntimeWorldHandle handler = this.createWorld();
			this.handler = handler;
			return handler.asWorld();
		}
		return this.handler.asWorld();
	}

	public void deleteWorld() {
		if(this.handler != null) {
			this.handler.delete();
		}
	}

	public void visit(PlayerEntity player) {
		ServerWorld world = this.getWorld();
		FabricDimensions.teleport(player, world, new TeleportTarget(this.spawnPos, new Vec3d(0, 0, 0), 0, 0));
	}
}
