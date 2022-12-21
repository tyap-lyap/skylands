package skylands.event;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import skylands.logic.Island;
import skylands.logic.Member;
import skylands.logic.Skylands;
import skylands.util.Texts;
import skylands.util.UpdateChecker;
import skylands.util.Worlds;

@SuppressWarnings("unused")
public class PlayerConnectEvent {

	public static void onJoin(MinecraftServer server, ServerPlayerEntity player) {
		Skylands skylands = Skylands.instance;

		if(player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.LEAVE_GAME)) == 0) {
			if(skylands.config.createIslandOnPlayerJoin) {
				Island island = skylands.islands.create(player);
				island.onFirstLoad();
				island.visitAsMember(player);
				player.sendMessage(Texts.prefixed("message.skylands.island_create.success"));
			}
		}

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
				FabricDimensions.teleport(player, server.getOverworld(), new TeleportTarget(Skylands.instance.hub.pos, new Vec3d(0, 0, 0), 0, 0));
			}
		});

		if(skylands.config.updateCheckerEnabled) {
			UpdateChecker.onPlayerJoin(player);
		}
	}

	public static void onLeave(MinecraftServer server, ServerPlayerEntity player) {

	}
}
