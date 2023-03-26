package skylands.event;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import skylands.logic.Member;
import skylands.logic.Skylands;
import skylands.util.Texts;
import skylands.util.UpdateChecker;
import skylands.util.Worlds;

import java.util.Set;

@SuppressWarnings("unused")
public class PlayerConnectEvent {

	public static void onJoin(MinecraftServer server, ServerPlayerEntity player) {

		if(server.getFile("hub_template").exists()) {
			if(player.getWorld().getRegistryKey().equals(World.OVERWORLD)) {
				Skylands.getInstance().hub.visit(player);
			}
		}

		Skylands skylands = Skylands.instance;

		skylands.islands.get(player).ifPresent(island -> {
			island.owner.name = player.getName().getString();
		});
		skylands.islands.stuck.forEach(island -> {
			for(Member member : island.members) {
				if(member.uuid.equals(player.getUuid())) {
					member.name = player.getName().getString();
				}
			}
			for(Member bannedMember : island.bans) {
				if(bannedMember.uuid.equals(player.getUuid())) {
					bannedMember.name = player.getName().getString();
				}
			}
		});

		Worlds.getIsland(player.getWorld()).ifPresent(island -> {
			if(!island.isMember(player) && island.isBanned(player)) {
				player.sendMessage(Texts.prefixed("message.skylands.ban_player.ban", map -> map.put("%owner%", island.owner.name)));
				player.sendMessage(Texts.prefixed("message.skylands.hub_visit"));
				var pos = Skylands.instance.hub.pos;
				player.teleport(server.getOverworld(), pos.getX(), pos.getY(), pos.getZ(), Set.of(), 0, 0);
			}
		});

		if(Skylands.config.updateCheckerEnabled) {
			UpdateChecker.onPlayerJoin(player);
		}
	}

	public static void onLeave(MinecraftServer server, ServerPlayerEntity player) {

	}
}
