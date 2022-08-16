package skylands.event;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import skylands.Mod;
import skylands.logic.Member;
import skylands.logic.Skylands;

import java.util.UUID;

@SuppressWarnings("unused")
public class PlayerConnectEvent {

	public static void onJoin(MinecraftServer server, ServerPlayerEntity player) {
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
				player.sendMessage(Text.of("Skylands > You have been banned from visiting " + island.get().owner.name + "'s Island!"));
				player.sendMessage(Text.of("Skylands > Teleporting to the Hub!"));
				FabricDimensions.teleport(player, server.getOverworld(), new TeleportTarget(Skylands.instance.hub.pos, new Vec3d(0, 0, 0), 0, 0));
			}
		}
	}

	public static void onLeave(MinecraftServer server, ServerPlayerEntity player) {

	}
}
