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
import net.minecraft.util.BlockMirror;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;
import skylands.SkylandsMod;
import skylands.util.Players;
import skylands.util.Texts;
import xyz.nucleoid.fantasy.Fantasy;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.fantasy.RuntimeWorldHandle;

import java.time.Instant;
import java.util.ArrayList;
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

	public boolean locked = false;
	public Vec3d spawnPos = new Vec3d(0.5D, 75D, 0.5D);
	public Vec3d visitsPos = new Vec3d(0.5D, 75D, 0.5D);
	public boolean hasNether = false;

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
		for (int i = 0; i < membersSize; i++) {
			NbtCompound member = membersNbt.getCompound(String.valueOf(i));
			island.members.add(Member.fromNbt(member));
		}

		NbtCompound bansNbt = nbt.getCompound("bans");
		int bansSize = bansNbt.getInt("size");
		for (int i = 0; i < bansSize; i++) {
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
		for (int i = 0; i < this.members.size(); i++) {
			Member member = this.members.get(i);
			NbtCompound memberNbt = member.toNbt();
			membersNbt.put(Integer.toString(i), memberNbt);
		}
		nbt.put("members", membersNbt);

		NbtCompound bansNbt = new NbtCompound();
		bansNbt.putInt("size", this.bans.size());
		for (int i = 0; i < this.bans.size(); i++) {
			Member bannedMember = this.bans.get(i);
			NbtCompound bannedNbt = bannedMember.toNbt();
			bansNbt.put(Integer.toString(i), bannedNbt);
		}
		nbt.put("bans", bansNbt);

		return nbt;
	}

	public boolean isMember(PlayerEntity player) {
		if (this.owner.uuid.equals(player.getUuid())) {
			return true;
		}
		for (var member : this.members) {
			if (member.uuid.equals(player.getUuid())) return true;
		}
		return false;
	}

	public boolean isMember(String name) {
		if (this.owner.name.equals(name)) {
			return true;
		}
		for (var member : this.members) {
			if (member.name.equals(name)) return true;
		}
		return false;
	}

	public boolean isBanned(PlayerEntity player) {
		for (var bannedMember : this.bans) {
			if (bannedMember.uuid.equals(player.getUuid())) return true;
		}
		return false;
	}

	public boolean isBanned(String player) {
		for (var bannedMember : this.bans) {
			if (bannedMember.name.equals(player)) return true;
		}
		return false;
	}

	public RuntimeWorldHandle getHandler() {
		if (this.islandConfig == null) {
			this.islandConfig = createIslandConfig();
		}
		return this.fantasy.getOrOpenPersistentWorld(SkylandsMod.id(this.owner.uuid.toString()), this.islandConfig);
	}

	private RuntimeWorldConfig createIslandConfig() {
		return new RuntimeWorldConfig()
				.setDimensionType(DimensionTypes.OVERWORLD)
				.setGenerator(server.getOverworld().getChunkManager().getChunkGenerator())
				.setDifficulty(Difficulty.NORMAL)
				.setShouldTickTime(true);
	}

	public RuntimeWorldHandle getNetherHandler() {
		if (this.netherConfig == null) {
			this.netherConfig = createNetherConfig();
		}
		return this.fantasy.getOrOpenPersistentWorld(new Identifier("nether", this.owner.uuid.toString()), this.netherConfig);
	}

	private RuntimeWorldConfig createNetherConfig() {
		ServerWorld nether = server.getWorld(World.NETHER);
		assert nether != null;
		return new RuntimeWorldConfig()
				.setDimensionType(DimensionTypes.THE_NETHER)
				.setGenerator(nether.getChunkManager().getChunkGenerator())
				.setDifficulty(Difficulty.NORMAL)
				.setShouldTickTime(true);
	}

	public ServerWorld getNether() {
		RuntimeWorldHandle handler = this.getNetherHandler();
		handler.setTickWhenEmpty(false);
		ServerWorld world = handler.asWorld();
		if (!this.hasNether) this.onFirstNetherLoad(world);
		return world;
	}

	public ServerWorld getWorld() {
		RuntimeWorldHandle handler = this.getHandler();
		handler.setTickWhenEmpty(false);
		return handler.asWorld();
	}

	public void visitAsMember(PlayerEntity player) {
		ServerWorld world = this.getWorld();
		FabricDimensions.teleport(player, world, new TeleportTarget(this.spawnPos, new Vec3d(0, 0, 0), 0, 0));
	}

	public void visitAsVisitor(PlayerEntity player) {
		ServerWorld world = this.getWorld();
		FabricDimensions.teleport(player, world, new TeleportTarget(this.visitsPos, new Vec3d(0, 0, 0), 0, 0));

		Players.get(this.owner.name).ifPresent(owner -> {
			if (!player.getUuid().equals(owner.getUuid())) {
				owner.sendMessage(Texts.prefixed("message.skylands.island_visit.visit", map -> map.put("%visitor%", player.getName().getString())));
			}
		});
	}

	public void onFirstLoad() {
		ServerWorld world = this.getWorld();
		StructureTemplate structure = server.getStructureTemplateManager().getTemplateOrBlank(SkylandsMod.id("start_island"));
		StructurePlacementData data = new StructurePlacementData().setMirror(BlockMirror.NONE).setIgnoreEntities(true);
		structure.place(world, new BlockPos(-7, 65, -7), new BlockPos(0, 0, 0), data, world.getRandom(), Block.NOTIFY_ALL);
	}

	void onFirstNetherLoad(ServerWorld world) {
		if (this.hasNether) return;

		MinecraftServer server = world.getServer();

		StructureTemplate structure = server.getStructureTemplateManager().getTemplateOrBlank(SkylandsMod.id("nether_island"));
		StructurePlacementData data = new StructurePlacementData().setMirror(BlockMirror.NONE).setIgnoreEntities(true);
		structure.place(world, new BlockPos(-7, 65, -7), new BlockPos(0, 0, 0), data, world.getRandom(), Block.NOTIFY_ALL);

		this.hasNether = true;
	}
}
