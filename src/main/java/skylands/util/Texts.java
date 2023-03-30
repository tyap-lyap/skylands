package skylands.util;

import com.mojang.brigadier.context.CommandContext;
import eu.pb4.placeholders.api.TextParserUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Language;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Texts {
	public static String prefixTranslationKey = "message.skylands.prefix";

	public static void prefixed(CommandContext<ServerCommandSource> context, String key, Consumer<Map<String, String>> builder) {
		context.getSource().sendFeedback(Texts.prefixed(key, builder), false);
	}

	public static void prefixed(PlayerEntity player, String key, Consumer<Map<String, String>> builder) {
		player.sendMessage(Texts.prefixed(key, builder));
	}

	public static Text prefixed(String key, Consumer<Map<String, String>> builder) {
		String prefix = getPrefix();
		String text = Language.getInstance().get(key);
		Map<String, String> placeholders = new HashMap<>();
		builder.accept(placeholders);

		for(String k : placeholders.keySet()) {
			String v = placeholders.get(k);
			text = text.replaceAll(k, v);
		}
		text = prefix + text;
		return TextParserUtils.formatText(text);
	}

	public static Text prefixed(String key) {
		return prefixed(key, (m) -> {});
	}

	public static Text of(String key, Consumer<Map<String, String>> builder) {
		String text = Language.getInstance().get(key);
		Map<String, String> placeholders = new HashMap<>();
		builder.accept(placeholders);

		for(String k : placeholders.keySet()) {
			String v = placeholders.get(k);
			text = text.replaceAll(k, v);
		}
		return TextParserUtils.formatText(text);
	}

	public static Text of(String key) {
		return of(key, (m) -> {});
	}

	public static void setPrefix(String key) {
		prefixTranslationKey = key;
	}

	public static String getPrefix() {
		return Language.getInstance().get(prefixTranslationKey);
	}
}
