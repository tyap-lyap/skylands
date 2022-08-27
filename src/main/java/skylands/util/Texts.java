package skylands.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Language;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Texts {

	public static MutableText prefixed(String key, Consumer<Map<String, String>> builder) {
		String prefix = getPrefix();
		String text = Language.getInstance().get(key);
		Map<String, String> placeholders = new HashMap<>();
		builder.accept(placeholders);

		for(String k : placeholders.keySet()) {
			String v = placeholders.get(k);
			text = text.replaceAll(k, v);
		}
		text = prefix + text;
		return Text.literal(text);
	}

	public static MutableText prefixed(String key) {
		return prefixed(key, (m) -> {});
	}

	public static MutableText of(String key, Consumer<Map<String, String>> builder) {
		String text = Language.getInstance().get(key);
		Map<String, String> placeholders = new HashMap<>();
		builder.accept(placeholders);

		for(String k : placeholders.keySet()) {
			String v = placeholders.get(k);
			text = text.replaceAll(k, v);
		}
		return Text.literal(text);
	}

	public static MutableText of(String key) {
		return of(key, (m) -> {});
	}

	public static String getPrefix() {
		return Language.getInstance().get("message.skylands.prefix");
	}
}
