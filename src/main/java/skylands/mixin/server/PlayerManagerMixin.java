package skylands.mixin.server;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import skylands.logic.Island;
import skylands.logic.Skylands;
import skylands.util.SkylandsTexts;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

	@Inject(method = "onPlayerConnect", at = @At("TAIL"))
	void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
		if(player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.LEAVE_GAME)) == 0) {
			if(Skylands.config.createIslandOnPlayerJoin) {
				Island island = Skylands.instance.islands.create(player);
				if(Skylands.config.teleportAfterIslandCreation) {
					island.visitAsMember(player);
				}
				player.sendMessage(SkylandsTexts.prefixed("message.skylands.island_create.success"));
			}
		}

		if(player.getWorld().getRegistryKey().equals(World.OVERWORLD) && Skylands.config.forceHubSpawnPos) {
			Skylands.instance.hub.visit(player);
		}
	}
}
