package skylands.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.block.Block;
import net.minecraft.command.argument.Vec3ArgumentType;
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

public class Commands {

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
						StructureTemplate structure = source.getServer().getStructureTemplateManager().getTemplateOrBlank(Mod.id("main"));
						StructurePlacementData data = new StructurePlacementData().setMirror(BlockMirror.NONE).setIgnoreEntities(true);
						structure.place(island.getWorld(), new BlockPos(-7, 65, -7), new BlockPos(0, 0, 0), data, player.getRandom(), Block.NOTIFY_ALL);
						player.sendMessage(Text.of("Skylands > Your island got successfully created, you can now visit it with the '/sl home' command!"));
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
			})));
			dispatcher.register(CommandManager.literal("force-sl").then(CommandManager.literal("set-hub-pos").then(CommandManager.argument("position", Vec3ArgumentType.vec3()).executes(context -> {
				var pos = Vec3ArgumentType.getPosArgument(context, "position");
				Skylands.instance.hub.pos = pos.toAbsolutePos(context.getSource());
				return 1;
			}))));
		});
	}
}
