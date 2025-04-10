package skylands.event;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import skylands.logic.Member;
import skylands.logic.Skylands;
import skylands.util.SkylandsTexts;
import skylands.util.UpdateChecker;
import skylands.util.SkylandsWorlds;

import java.util.Set;

@SuppressWarnings("unused")
public class PlayerConnectEvent implements ServerPlayConnectionEvents.Join, ServerPlayConnectionEvents.Disconnect {
	static final PlayerConnectEvent INSTANCE = new PlayerConnectEvent();

	@Override
	public void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
		ServerPlayerEntity player = handler.getPlayer();

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

		SkylandsWorlds.getIsland(player.getWorld()).ifPresent(island -> {
			if(!island.isMember(player) && island.isBanned(player)) {
				player.sendMessage(SkylandsTexts.prefixed("message.skylands.ban_player.ban", map -> map.put("%owner%", island.owner.name)));
				player.sendMessage(SkylandsTexts.prefixed("message.skylands.hub_visit"));
				var pos = Skylands.instance.hub.spawnPos;
				player.teleport(server.getOverworld(), pos.x, pos.y, pos.z, Set.of(), pos.yaw, pos.pitch);
			}
		});

		if(Skylands.config.updateCheckerEnabled) {
			UpdateChecker.onPlayerJoin(player);
		}
	}

	@Override
	public void onPlayDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server) {

	}
}
