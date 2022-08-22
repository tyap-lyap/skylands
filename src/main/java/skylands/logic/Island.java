package skylands.logic;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.text.Text;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import skylands.Mod;
import skylands.util.Players;
import xyz.nucleoid.fantasy.Fantasy;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.fantasy.RuntimeWorldHandle;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class Island {
	private static final Registry<StructureSet> EMPTY_STRUCTURE_REGISTRY = new SimpleRegistry<>(Registry.STRUCTURE_SET_KEY, Lifecycle.stable(), (x) -> null).freeze();
	MinecraftServer server = Skylands.instance.server;
	Fantasy fantasy = Skylands.instance.fantasy;
	RuntimeWorldConfig islandConfig = null;
	RuntimeWorldConfig netherConfig = null;
	public Member owner;
	public ArrayList<Member> members = new ArrayList<>();
	public ArrayList<Member> bans = new ArrayList<>();

	public Vec3d spawnPos = new Vec3d(0.5D, 75D, 0.5D);
	public boolean hasNether = false;

	public Island(UUID uuid, String name) {
		this.owner = new Member(uuid, name);
	}

	public Island(PlayerEntity owner) {
		this.owner = new Member(owner);
	}

	public Island(Member owner) {
		this.owner = owner;
	}

	public static Island fromNbt(NbtCompound nbt) {
		Island island = new Island(Member.fromNbt(nbt.getCompound("owner")));
		island.hasNether = nbt.getBoolean("hasNether");

		NbtCompound membersNbt = nbt.getCompound("members");
		int membersSize = membersNbt.getInt("size");
		for(int i = 0; i < membersSize; i++) {
			NbtCompound member = membersNbt.getCompound(String.valueOf(i));
			island.members.add(Member.fromNbt(member));
		}

		NbtCompound bansNbt = nbt.getCompound("bans");
		int bansSize = bansNbt.getInt("size");
		for(int i = 0; i < bansSize; i++) {
			NbtCompound member = bansNbt.getCompound(String.valueOf(i));
			island.bans.add(Member.fromNbt(member));
		}

		return island;
	}

	public NbtCompound toNbt() {
		NbtCompound nbt = new NbtCompound();
		nbt.put("owner", this.owner.toNbt());
		nbt.putBoolean("hasNether", this.hasNether);

		NbtCompound membersNbt = new NbtCompound();
		membersNbt.putInt("size", this.members.size());
		for(int i = 0; i < this.members.size(); i++) {
			Member member = this.members.get(i);
			NbtCompound memberNbt = member.toNbt();
			membersNbt.put(Integer.toString(i), memberNbt);
		}
		nbt.put("members", membersNbt);

		NbtCompound bansNbt = new NbtCompound();
		bansNbt.putInt("size", this.bans.size());
		for(int i = 0; i < this.bans.size(); i++) {
			Member bannedMember = this.bans.get(i);
			NbtCompound bannedNbt = bannedMember.toNbt();
			bansNbt.put(Integer.toString(i), bannedNbt);
		}
		nbt.put("bans", bansNbt);

		return nbt;
	}

	public boolean isMember(PlayerEntity player) {
		if(this.owner.uuid.equals(player.getUuid())) {
			return true;
		}
		for(var member : this.members) {
			if(member.uuid.equals(player.getUuid())) return true;
		}
		return false;
	}

	public boolean isMember(String name) {
		if(this.owner.name.equals(name)) {
			return true;
		}
		for(var member : this.members) {
			if(member.name.equals(name)) return true;
		}
		return false;
	}

	public boolean isBanned(PlayerEntity player) {
		for(var bannedMember : this.bans) {
			if(bannedMember.uuid.equals(player.getUuid())) return true;
		}
		return false;
	}

	public RuntimeWorldHandle getHandler() {
		if(this.islandConfig == null) {
			this.islandConfig = createIslandConfig();
		}
		return this.fantasy.getOrOpenPersistentWorld(Mod.id(this.owner.uuid.toString()), this.islandConfig);
	}

	private RuntimeWorldConfig createIslandConfig() {
		FlatChunkGeneratorConfig flat = new FlatChunkGeneratorConfig(Optional.empty(), BuiltinRegistries.BIOME);
		flat.setBiome(this.server.getRegistryManager().get(Registry.BIOME_KEY).getOrCreateEntry(BiomeKeys.PLAINS));
		FlatChunkGenerator generator = new FlatChunkGenerator(EMPTY_STRUCTURE_REGISTRY, flat);

		return new RuntimeWorldConfig()
				.setDimensionType(DimensionTypes.OVERWORLD)
				.setGenerator(generator)
				.setDifficulty(Difficulty.NORMAL)
				.setSeed(123L);
	}

	public RuntimeWorldHandle getNetherHandler() {
		if(this.netherConfig == null) {
			this.netherConfig = createNetherConfig();
		}
		return this.fantasy.getOrOpenPersistentWorld(new Identifier("nether", this.owner.uuid.toString()), this.netherConfig);
	}

	private RuntimeWorldConfig createNetherConfig() {
		FlatChunkGeneratorConfig flat = new FlatChunkGeneratorConfig(Optional.empty(), BuiltinRegistries.BIOME);
		flat.setBiome(this.server.getRegistryManager().get(Registry.BIOME_KEY).getOrCreateEntry(BiomeKeys.NETHER_WASTES));
		FlatChunkGenerator generator = new FlatChunkGenerator(EMPTY_STRUCTURE_REGISTRY, flat);

		return new RuntimeWorldConfig()
				.setDimensionType(DimensionTypes.THE_NETHER)
				.setGenerator(generator)
				.setDifficulty(Difficulty.NORMAL)
				.setSeed(123L);
	}

	public ServerWorld getNether() {
		RuntimeWorldHandle handler = this.getNetherHandler();
		handler.setTickWhenEmpty(false);
		ServerWorld world = handler.asWorld();
		if(!this.hasNether) this.onFirstNetherLoad(world);
		return world;
	}

	public ServerWorld getWorld() {
		RuntimeWorldHandle handler = this.getHandler();
		handler.setTickWhenEmpty(false);
		return handler.asWorld();
	}

	public void visit(PlayerEntity player) {
		ServerWorld world = this.getWorld();
		FabricDimensions.teleport(player, world, new TeleportTarget(this.spawnPos, new Vec3d(0, 0, 0), 0, 0));
		Players.get(this.owner.name).ifPresent(owner -> {
			if(!player.getUuid().equals(owner.getUuid())) {
				owner.sendMessage(Text.of("Skylands > " + player.getName().getString() + " visited your Island!"));
			}
		});
	}

	void onFirstNetherLoad(ServerWorld world) {
		if(this.hasNether) return;

		MinecraftServer server = world.getServer();

		StructureTemplate structure = server.getStructureTemplateManager().getTemplateOrBlank(Mod.id("nether_island"));
		StructurePlacementData data = new StructurePlacementData().setMirror(BlockMirror.NONE).setIgnoreEntities(true);
		structure.place(world, new BlockPos(-7, 65, -7), new BlockPos(0, 0, 0), data, world.getRandom(), Block.NOTIFY_ALL);

		this.hasNether = true;
	}
}
