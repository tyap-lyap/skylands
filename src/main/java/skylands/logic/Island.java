package skylands.logic;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.Difficulty;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import skylands.Mod;
import skylands.util.Players;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.fantasy.RuntimeWorldHandle;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class Island {
	public Member owner;
	public ArrayList<Member> members = new ArrayList<>();
	public ArrayList<Member> bans = new ArrayList<>();

	public Vec3d spawnPos = new Vec3d(0.5D, 75D, 0.5D);

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
		FlatChunkGeneratorConfig flat = new FlatChunkGeneratorConfig(Optional.empty(), BuiltinRegistries.BIOME);
		var generator = new FlatChunkGenerator(BuiltinRegistries.STRUCTURE_SET, flat);
//		long seed = Long.parseLong(RandomStringUtils.randomNumeric(9));

		RuntimeWorldConfig config = new RuntimeWorldConfig()
				.setDimensionType(DimensionTypes.OVERWORLD)
				.setGenerator(generator)
				.setDifficulty(Difficulty.HARD)
				.setSeed(123L);

		return Skylands.instance.fantasy.getOrOpenPersistentWorld(Mod.id(this.owner.uuid.toString()), config);
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
}
