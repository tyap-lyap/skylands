package skylands.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.command.argument.BlockPosArgumentType.blockPos;
import static net.minecraft.command.argument.EntityArgumentType.player;

public class ModCommands {

	public static void init() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> ModCommands.register(dispatcher));
	}

	private static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("sl").then(literal("create").executes(context -> {
			var source = context.getSource();
			var player = source.getPlayer();
			if(player != null) {
				Create.cmd(player);
			}
			return 1;
		})));
		dispatcher.register(literal("sl").then(literal("hub").executes(context -> {
			var source = context.getSource();
			var player = source.getPlayer();
			MinecraftServer server = source.getServer();
			if(player != null) {
				Hub.visit(player, server);
			}
			return 1;
		})));
		dispatcher.register(literal("sl").then(literal("home").executes(context -> {
			var player = context.getSource().getPlayer();
			if(player != null) {
				Home.cmd(player);
			}
			return 1;
		})));
		dispatcher.register(literal("sl").then(literal("home").then(argument("player", word()).executes(context -> {
			var ownerName = StringArgumentType.getString(context, "player");
			var visitor = context.getSource().getPlayer();
			if(visitor != null) {
				Home.cmd(visitor, ownerName);
			}
			return 1;
		}))));
		dispatcher.register(literal("sl").then(literal("visit").then(argument("player", player()).executes(context -> {
			var visitor = context.getSource().getPlayer();
			var owner = EntityArgumentType.getPlayer(context, "player");
			if(visitor != null && owner != null) {
				Visit.cmd(visitor, owner);
			}
			return 1;
		}))));
		dispatcher.register(literal("sl").then(literal("add-member").then(argument("player", player()).executes(context -> {
			var player = context.getSource().getPlayer();
			var newcomer = EntityArgumentType.getPlayer(context, "player");
			if(player != null && newcomer != null) {
				InviteMembers.add(player, newcomer);
			}
			return 1;
		}))));
		dispatcher.register(literal("sl").then(literal("ban").then(argument("player", player()).executes(context -> {
			var player = context.getSource().getPlayer();
			var bannedPlayer = EntityArgumentType.getPlayer(context, "player");
			if(player != null && bannedPlayer != null) {
				Ban.cmd(player, bannedPlayer);
			}
			return 1;
		}))));
		dispatcher.register(literal("sl").then(literal("remove-member").then(argument("player", word()).executes(context -> {
			String memberToRemove = StringArgumentType.getString(context, "player");
			var player = context.getSource().getPlayer();
			if(player != null) {
				InviteMembers.remove(player, memberToRemove);
			}
			return 1;
		}))));
		dispatcher.register(literal("sl").then(literal("help").executes(context -> {
			ServerPlayerEntity player = context.getSource().getPlayer();
			if(player != null) {
				Help.cmd(player);
			}
			return 1;
		})));

		dispatcher.register(literal("force-sl").requires(source -> source.hasPermissionLevel(4)).then(literal("set-hub-pos").then(argument("position", blockPos()).executes(context -> {
			var pos = BlockPosArgumentType.getBlockPos(context, "position");
			var source = context.getSource();
			Hub.setPos(pos, source);
			return 1;
		}))).then(literal("delete-island").then(CommandManager.argument("player", word()).executes(context -> {
			var player = StringArgumentType.getString(context, "player");
			return 1;
		}))).then(literal("toggle-hub-protection").executes(context -> {
			Hub.toggleProtection(context.getSource());
			return 1;
		})));

		dispatcher.register(literal("sl").then(literal("accept")).then(argument("player", word()).executes(context -> {
			String inviter = StringArgumentType.getString(context, "player");
			var player = context.getSource().getPlayer();

			if(player != null) {
				Accept.cmd(player, inviter);
			}
			return 1;
		})));
	}
}
