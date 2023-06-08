package skylands.logic;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.RandomSeed;
import net.minecraft.world.Difficulty;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import org.apache.commons.io.FileUtils;
import skylands.SkylandsMod;
import skylands.api.SkylandsAPI;
import skylands.util.SkylandsTexts;
import xyz.nucleoid.fantasy.Fantasy;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.fantasy.RuntimeWorldHandle;

import java.io.File;
import java.time.Instant;
import java.util.*;

public class Island {
	MinecraftServer server = Skylands.instance.server;
	Skylands skylands = Skylands.instance;
	Fantasy fantasy = Skylands.instance.fantasy;
	RuntimeWorldConfig islandConfig = null;
	RuntimeWorldConfig netherConfig = null;
	public Member owner;
	public ArrayList<Member> members = new ArrayList<>();
	public ArrayList<Member> bans = new ArrayList<>();

	public boolean locked = false;
	public Vec3d spawnPos = Skylands.config.defaultSpawnPos.toVec();
	public Vec3d visitsPos = Skylands.config.defaultVisitsPos.toVec();
	public boolean hasNether = false;
	public long seed = 0L;
	/**
	 * Mark indicates that this island was just created and wasn't visited yet
	 */
	boolean freshCreated = false;
	public Instant created = Instant.now();

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
		island.created = Instant.parse(nbt.getString("created"));
		island.locked = nbt.getBoolean("locked");
		island.seed = nbt.getLong("seed");
		island.freshCreated = nbt.getBoolean("freshCreated");

		var spawnPosNbt = nbt.getCompound("spawnPos");
		double spawnPosX = spawnPosNbt.getDouble("x");
		double spawnPosY = spawnPosNbt.getDouble("y");
		double spawnPosZ = spawnPosNbt.getDouble("z");
		island.spawnPos = new Vec3d(spawnPosX, spawnPosY, spawnPosZ);

		var visitsPosNbt = nbt.getCompound("visitsPos");
		double visitsPosX = visitsPosNbt.getDouble("x");
		double visitsPosY = visitsPosNbt.getDouble("y");
		double visitsPosZ = visitsPosNbt.getDouble("z");
		island.visitsPos = new Vec3d(visitsPosX, visitsPosY, visitsPosZ);

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
		nbt.putString("created", this.created.toString());
		nbt.putBoolean("locked", this.locked);
		nbt.putLong("seed", this.seed);
		nbt.putBoolean("freshCreated", this.freshCreated);

		NbtCompound spawnPosNbt = new NbtCompound();
		spawnPosNbt.putDouble("x", this.spawnPos.getX());
		spawnPosNbt.putDouble("y", this.spawnPos.getY());
		spawnPosNbt.putDouble("z", this.spawnPos.getZ());
		nbt.put("spawnPos", spawnPosNbt);

		NbtCompound visitsPosNbt = new NbtCompound();
		visitsPosNbt.putDouble("x", this.visitsPos.getX());
		visitsPosNbt.putDouble("y", this.visitsPos.getY());
		visitsPosNbt.putDouble("z", this.visitsPos.getZ());
		nbt.put("visitsPos", visitsPosNbt);

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

	public boolean isBanned(String player) {
		for(var bannedMember : this.bans) {
			if(bannedMember.name.equals(player)) return true;
		}
		return false;
	}

	public long getSeed() {
		if (this.seed == 0) this.seed = RandomSeed.getSeed();
		return this.seed;
	}

	/**
	 * @return list of players currently on this island
	 */
	public List<ServerPlayerEntity> getPlayers() {
		return server.getPlayerManager().getPlayerList().stream().filter(player -> {
			var island = SkylandsAPI.getIsland(player);
			return island.isPresent() && island.get().equals(this);
		}).toList();
	}

	public Optional<ServerPlayerEntity> getOwner() {
		return Optional.ofNullable(server.getPlayerManager().getPlayer(this.owner.uuid));
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Island isl) return isl.owner.uuid.equals(this.owner.uuid);
		return super.equals(obj);
	}

	public RuntimeWorldHandle getHandler() {
		if(this.islandConfig == null) {
			this.islandConfig = createIslandConfig();
		}
		return this.fantasy.getOrOpenPersistentWorld(SkylandsMod.id(this.owner.uuid.toString()), this.islandConfig);
	}

	private RuntimeWorldConfig createIslandConfig() {
		var biome = this.server.getRegistryManager().get(RegistryKeys.BIOME).getEntry(this.server.getRegistryManager().get(RegistryKeys.BIOME).getOrThrow(BiomeKeys.PLAINS));
		FlatChunkGeneratorConfig flat = new FlatChunkGeneratorConfig(Optional.of(RegistryEntryList.of()), biome, List.of());
		FlatChunkGenerator generator = new FlatChunkGenerator(flat);

		return new RuntimeWorldConfig()
				.setDimensionType(DimensionTypes.OVERWORLD)
				.setGenerator(generator)
				.setDifficulty(Difficulty.NORMAL)
				.setShouldTickTime(true)
				.setSeed(this.getSeed());
	}

	public RuntimeWorldHandle getNetherHandler() {
		if(this.netherConfig == null) {
			this.netherConfig = createNetherConfig();
		}
		copyNetherTemplate();
		return this.fantasy.getOrOpenPersistentWorld(new Identifier("nether", this.owner.uuid.toString()), this.netherConfig);
	}

	public void unload() {
		getHandler().unload();
		if(hasNether) getNetherHandler().unload();
	}

	void copyNetherTemplate() {
		try {
			File netherTemplate = server.getFile("nether_template");
			String path = server.getSavePath(WorldSavePath.DATAPACKS).toFile().toString().replace("\\datapacks", "") + "\\dimensions\\nether\\" + owner.uuid.toString();
			File lock = new File(path + "\\copied.lock");

			if(netherTemplate.exists() && !lock.exists()) {
				FileUtils.copyDirectory(netherTemplate, new File(path));
				lock.createNewFile();
			}
		}
		catch (Exception e) {
			SkylandsMod.LOGGER.error("Failed to copy nether template due to an exception: " + e);
			e.printStackTrace();
		}
	}

	private RuntimeWorldConfig createNetherConfig() {
		var biome = this.server.getRegistryManager().get(RegistryKeys.BIOME).getEntry(this.server.getRegistryManager().get(RegistryKeys.BIOME).getOrThrow(BiomeKeys.NETHER_WASTES));
		FlatChunkGeneratorConfig flat = new FlatChunkGeneratorConfig(Optional.of(RegistryEntryList.of()), biome, List.of());
		FlatChunkGenerator generator = new FlatChunkGenerator(flat);

		return new RuntimeWorldConfig()
				.setDimensionType(DimensionTypes.THE_NETHER)
				.setGenerator(generator)
				.setDifficulty(Difficulty.NORMAL)
				.setShouldTickTime(true)
				.setSeed(this.getSeed());
	}

	public ServerWorld getEnd() {
		// todo
		// may coming soon?
		return null;
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

	public void visit(PlayerEntity visitor, Vec3d pos, float yaw, float pitch) {
		ServerWorld world = this.getWorld();
		visitor.teleport(world, pos.getX(), pos.getY(), pos.getZ(), Set.of(), yaw, pitch);

		if(!isMember(visitor)) {
			this.getOwner().ifPresent(owner -> {
				if(!visitor.getUuid().equals(owner.getUuid())) {
					owner.sendMessage(SkylandsTexts.prefixed("message.skylands.island_visit.visit", map -> map.put("%visitor%", visitor.getName().getString())));
				}
			});
		}

		SkylandsAPI.ON_ISLAND_VISIT.invoker().invoke(visitor, world, this);

		if (this.freshCreated) {
			this.onFirstLoad(visitor);
			this.freshCreated = false;
		}
	}

	public void visitAsMember(PlayerEntity player) {
		this.visit(player, this.spawnPos, Skylands.config.defaultSpawnPos.yaw, Skylands.config.defaultSpawnPos.pitch);
	}

	public void visitAsVisitor(PlayerEntity player) {
		this.visit(player, this.visitsPos, Skylands.config.defaultVisitsPos.yaw, Skylands.config.defaultVisitsPos.pitch);
	}

	public void onFirstLoad(PlayerEntity player) {
		ServerWorld world = this.getWorld();
		if(!server.getFile("island_template").exists()) {
			StructureTemplate structure = server.getStructureTemplateManager().getTemplateOrBlank(SkylandsMod.id("start_island"));
			StructurePlacementData data = new StructurePlacementData().setMirror(BlockMirror.NONE).setIgnoreEntities(true);
			structure.place(world, new BlockPos(-7, 65, -7), new BlockPos(0, 0, 0), data, world.getRandom(), Block.NOTIFY_ALL);
		}
		SkylandsAPI.ON_ISLAND_FIRST_LOAD.invoker().invoke(player, world, this);
	}

	void onFirstNetherLoad(ServerWorld world) {
		if(this.hasNether) return;

		MinecraftServer server = world.getServer();

		StructureTemplate structure = server.getStructureTemplateManager().getTemplateOrBlank(SkylandsMod.id("nether_island"));
		StructurePlacementData data = new StructurePlacementData().setMirror(BlockMirror.NONE).setIgnoreEntities(true);
		structure.place(world, new BlockPos(-7, 65, -7), new BlockPos(0, 0, 0), data, world.getRandom(), Block.NOTIFY_ALL);
		SkylandsAPI.ON_NETHER_FIRST_LOAD.invoker().onLoad(world, this);

		this.hasNether = true;
	}
}
