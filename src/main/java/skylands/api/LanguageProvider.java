package skylands.api;

import net.minecraft.entity.player.PlayerEntity;

public interface LanguageProvider {

	default String getLanguage(PlayerEntity player) {
		return "en_us";
	}
}
