package skylands.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.block.Block;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.text.Text;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import skylands.Mod;
import skylands.logic.Skylands;

public class ModCommands {

	public static void init() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("sl").then(CommandManager.literal("create").executes(context -> {
				var source = context.getSource();
				var player = source.getPlayer();
				if(player != null) {
					Skylands.instance.islandStuck.get(player).ifPresentOrElse(island -> {
						player.sendMessage(Text.of("Skylands > You're already have an island!"));
					}, () -> {
						var island = Skylands.instance.islandStuck.create(player);
						var world = island.getWorld();
						StructureTemplate structure = source.getServer().getStructureTemplateManager().getTemplateOrBlank(Mod.id("main"));
						StructurePlacementData data = new StructurePlacementData().setMirror(BlockMirror.NONE).setIgnoreEntities(true);
						structure.place(world, new BlockPos(-7, 65, -7), new BlockPos(0, 0, 0), data, player.getRandom(), Block.NOTIFY_ALL);
//						player.setSpawnPoint(world.getRegistryKey(), new BlockPos(island.spawnPos), 0, false, false);
						player.sendMessage(Text.of("Skylands > Your island got successfully created, you can now visit it with the \"/sl home\" command!"));
					});
				}
				return 1;
			})).then(CommandManager.literal("hub").executes(context -> {
				var source = context.getSource();
				var player = source.getPlayer();
				MinecraftServer server = source.getServer();
				if(player != null) {
					player.sendMessage(Text.of("Skylands > Teleporting to the Hub!"));
					FabricDimensions.teleport(player, server.getOverworld(), new TeleportTarget(Skylands.instance.hub.pos, new Vec3d(0, 0, 0), 0, 0));
				}
				return 1;
			})).then(CommandManager.literal("home").executes(context -> {
				var player = context.getSource().getPlayer();
				if(player != null) {
					Skylands.instance.islandStuck.get(player).ifPresentOrElse(island -> {
						if(player.getWorld().getRegistryKey().getValue().equals(Mod.id(player.getUuid().toString()))) {
							player.sendMessage(Text.of("Skylands > You are already on your island!"));
						}
						else {
							player.sendMessage(Text.of("Skylands > Teleporting to the Island!"));
							island.visit(player);
						}
					}, () -> {
						player.sendMessage(Text.of("Skylands > You don't have an island yet!"));
					});
				}
				return 1;
			})).then(CommandManager.literal("visit").then(CommandManager.argument("player", EntityArgumentType.player()).executes(context -> {
				var visitor = context.getSource().getPlayer();
				if(visitor != null) {
					PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
					Skylands.instance.islandStuck.get(player).ifPresentOrElse(island -> {
						visitor.sendMessage(Text.of("Skylands > Teleporting to the " + player.getName().getString() + "'s island!"));
						island.visit(visitor);
					}, () -> {
						visitor.sendMessage(Text.of("Skylands > " + player.getName().getString() + " doesn't have an island yet!"));
					});
				}
				return 1;
			}))));
			dispatcher.register(CommandManager.literal("force-sl").requires(source -> source.hasPermissionLevel(4)).then(CommandManager.literal("set-hub-pos").then(CommandManager.argument("position", BlockPosArgumentType.blockPos()).executes(context -> {
				var pos = BlockPosArgumentType.getBlockPos(context, "position");
				Skylands.instance.hub.pos = new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
				context.getSource().sendFeedback(Text.of("Skylands > Position of the hub got changed to " + pos), true);
				return 1;
			}))).then(CommandManager.literal("delete-island").then(CommandManager.argument("player", StringArgumentType.word()).executes(context -> {
				var player = StringArgumentType.getString(context, "player");
				return 1;
			}))));
		});
	}
}
