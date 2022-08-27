package skylands.event;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import nota.player.SongPlayer;
import skylands.Mod;
import skylands.logic.Member;
import skylands.logic.Skylands;
import skylands.util.Texts;

import java.util.UUID;

@SuppressWarnings("unused")
public class PlayerConnectEvent {

	public static void onJoin(MinecraftServer server, ServerPlayerEntity player) {
		SongPlayer sp = Skylands.instance.hub.songPlayer;
		if(sp != null) {
			sp.addPlayer(player);
		}

		Skylands.instance.islandStuck.get(player).ifPresent(island -> {
			island.owner.name = player.getName().getString();
		});
		Skylands.instance.islandStuck.islands.forEach(island -> {
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
		var world = player.getWorld();

		if(world.getRegistryKey().getValue().getNamespace().equals(Mod.MOD_ID)) {
			var uuid = UUID.fromString(world.getRegistryKey().getValue().getPath());
			var island = Skylands.instance.islandStuck.get(uuid);

			if(island.isPresent() && !island.get().isMember(player) && island.get().isBanned(player)) {
				player.sendMessage(Texts.prefixed("message.skylands.ban_player.ban", map -> map.put("%owner%", island.get().owner.name)));
				player.sendMessage(Texts.prefixed("message.skylands.hub_visit"));
				FabricDimensions.teleport(player, server.getOverworld(), new TeleportTarget(Skylands.instance.hub.pos, new Vec3d(0, 0, 0), 0, 0));
			}
		}
	}

	public static void onLeave(MinecraftServer server, ServerPlayerEntity player) {

	}
}
