package skylands.logic;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import nota.model.Playlist;
import nota.model.Song;
import nota.player.PositionSongPlayer;
import nota.utils.NBSDecoder;
import skylands.Mod;

import java.io.File;

public class Hub {
	public Vec3d pos = new Vec3d(0, 80, 0);
	public boolean hasProtection = false;
	public PositionSongPlayer songPlayer = null;

	public void initSongPlayer(MinecraftServer server) {
		Playlist playlist = loadSongs();
		if(playlist != null) {
			PositionSongPlayer sp = new PositionSongPlayer(playlist, server.getOverworld());
			sp.setDistance(128);
			sp.setId(new Identifier("skylands:hub_song_player"));
			sp.setBlockPos(new BlockPos(0, 64, 0));
			this.songPlayer = sp;
		}
	}

	private Playlist loadSongs() {
		try {
			Song home = NBSDecoder.parse(new File("songs/home.nbs"));
			Song mikado = NBSDecoder.parse(new File("songs/our_great_mikado.nbs"));
			Song howlsCastle = NBSDecoder.parse(new File("songs/merry_go_round_of_life.nbs"));
			return new Playlist(home, mikado, howlsCastle);
		}
		catch(Exception e) {
			Mod.LOGGER.info("Failed to load songs, disabling hub song player: " + e);
		}
		return null;
	}

	public void readFromNbt(NbtCompound nbt) {
		NbtCompound hubNbt = nbt.getCompound("hub");
		this.pos = new Vec3d(hubNbt.getDouble("x"), hubNbt.getDouble("y"), hubNbt.getDouble("z"));
		this.hasProtection = hubNbt.getBoolean("hasProtection");
	}

	public void writeToNbt(NbtCompound nbt) {
		NbtCompound hubNbt = new NbtCompound();
		hubNbt.putDouble("x", this.pos.x);
		hubNbt.putDouble("y", this.pos.y);
		hubNbt.putDouble("z", this.pos.z);
		hubNbt.putBoolean("hasProtection", this.hasProtection);
		nbt.put("hub", hubNbt);
	}
}
