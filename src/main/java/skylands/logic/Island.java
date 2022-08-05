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
import skylands.Mod;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.fantasy.RuntimeWorldHandle;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class Island {
	public UUID ownerUUID;
	public ArrayList<UUID> members = new ArrayList<>();

	public Vec3d spawnPos = new Vec3d(0.5D, 75D, 0.5D);

	public Island(UUID uuid) {
		this.ownerUUID = uuid;
	}

	public boolean isMember(PlayerEntity player) {
		if(ownerUUID.equals(player.getUuid())) return true;
		for(var uuid : members) {
			if(uuid.equals(player.getUuid())) return true;
		}
		return false;
	}

	private RuntimeWorldHandle getHandler() {
		FlatChunkGeneratorConfig flat = new FlatChunkGeneratorConfig(Optional.empty(), BuiltinRegistries.BIOME);
		var generator = new FlatChunkGenerator(BuiltinRegistries.STRUCTURE_SET, flat);
//		long seed = Long.parseLong(RandomStringUtils.randomNumeric(9));

		RuntimeWorldConfig config = new RuntimeWorldConfig()
				.setDimensionType(DimensionTypes.OVERWORLD)
				.setGenerator(generator)
				.setDifficulty(Difficulty.HARD)
				.setSeed(123L);

		return Skylands.instance.fantasy.getOrOpenPersistentWorld(Mod.id(this.ownerUUID.toString()), config);
	}

	public ServerWorld getWorld() {
		RuntimeWorldHandle handler = this.getHandler();
		return handler.asWorld();
	}

	public void deleteWorld() {
		RuntimeWorldHandle handler = this.getHandler();
		handler.delete();
	}

	public void visit(PlayerEntity player) {
		ServerWorld world = this.getWorld();
		FabricDimensions.teleport(player, world, new TeleportTarget(this.spawnPos, new Vec3d(0, 0, 0), 0, 0));
	}
}
