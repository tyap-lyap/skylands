package skylands.logic;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

import java.util.ArrayList;
import java.util.List;

public class Cutscenes {
	public static final Logic TEST = new Logic(new BlockPos(0, 64, 0));

	static ArrayList<Cutscene> cutscenes = new ArrayList<>();

	public static void onTick(MinecraftServer server) {
		cutscenes.forEach(Cutscene::tick);
		cutscenes.removeIf(cutscene -> cutscene.duration == cutscene.ticks);
	}

	public static void create(ServerWorld world, ServerPlayerEntity player, int duration) {
		cutscenes.add(new Cutscene(TEST, world, player, duration));
	}

	static class Cutscene {
		public ServerWorld world;
		public ServerPlayerEntity player;
		public ArmorStandEntity armorStand;
		public Logic logic;
		public int ticks = -1;
		public int duration;

		public Cutscene(Logic logic, ServerWorld world, ServerPlayerEntity player, int duration) {
			this.duration = duration;
			this.world = world;
			this.player = player;
			this.logic = logic.clone();
		}

		public void tick() {
			ticks++;
			if(ticks == 0) {
				this.onStart();
			}
			else {
				if(ticks == duration) {
					this.onEnd();
				}
				else {
					this.tickLogic();
				}
			}
		}

		public void tickLogic() {
			this.armorStand.setVelocity(new Vec3d(0.05D, 0D, 0D));

			if(player != null) {
				player.setCameraEntity(armorStand);
			}

			// 0 = +Z
			// 90 = -X
			// 180 = -Z
			// -90 = +X
		}

		public void onStart() {
			ArmorStandEntity entity = EntityType.ARMOR_STAND.create(world, null, null, player, player.getBlockPos(), SpawnReason.COMMAND, true, true);
			if (entity != null && player != null) {
				entity.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), -90, 0.0F);
				entity.setInvisible(true);
				world.spawnEntity(entity);
				player.networkHandler.sendPacket(new EntitySetHeadYawS2CPacket(entity, (byte)(360 + 90)));
				player.networkHandler.sendPacket(new EntitySetHeadYawS2CPacket(player, (byte)(360 + 90)));
				player.changeGameMode(GameMode.SPECTATOR);
				armorStand = entity;
			}
		}

		public void onEnd() {
			if(player != null) {
				player.changeGameMode(GameMode.SURVIVAL);
			}
			this.armorStand.remove(Entity.RemovalReason.DISCARDED);
		}

	}

	static class Logic implements Cloneable {
		public BlockPos finalPos;
		public ArrayList<Move> moves;
		public int currentMoveIndex = 0;

		public Logic(BlockPos finalPos, Move... moves) {
			this.finalPos = finalPos;
			this.moves = new ArrayList<>(List.of(moves));
		}

		public void onTick() {

		}

		@Override
		public Logic clone() {
			try {
				Logic clone = (Logic) super.clone();
				clone.moves = new ArrayList<>(this.moves);
				clone.finalPos = new BlockPos(this.finalPos);
				clone.currentMoveIndex = this.currentMoveIndex;
				return clone;
			}
			catch(CloneNotSupportedException e) {
				throw new AssertionError();
			}
		}
	}

	static class Move {
		public BlockPos from;
		public BlockPos to;
		public float speed;
		public Direction direction;
		public byte yaw;

		public Move(BlockPos from, BlockPos to, float speed, Direction direction) {
			this.from = from;
			this.to = to;
			this.speed = speed;
			this.direction = direction;
			switch(direction) {
				case UP, DOWN, SOUTH -> yaw = 0;
				case NORTH -> yaw = (byte) 180;
				case WEST -> yaw = 90;
				case EAST -> yaw = (byte)(360 + 90);
			}
		}

		public Vec3d getVec() {
			switch(direction) {
				case UP, DOWN, SOUTH -> {
					return new Vec3d(0, 0, speed);
				}
				case NORTH -> {
					return new Vec3d(0, 0, -speed);
				}
				case WEST -> {
					return new Vec3d(-speed, 0, 0);
				}
				case EAST -> {
					return new Vec3d(speed, 0, 0);
				}
			}
			return new Vec3d(0, 0, speed);
		}
	}
}
