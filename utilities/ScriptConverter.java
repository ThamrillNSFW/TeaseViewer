package utilities;

import java.util.Comparator;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.text.StringEscapeUtils;

public class ScriptConverter {

	public static String convertScript(String input) {
		if (!StringEscapeUtils.escapeJava(input).toLowerCase().contains("\\u")) {
			return input;
		}
		String output = input;
		Pattern pattern = Pattern.compile("((?:\\\\u[a-f0-9][a-f0-9][a-f0-9][a-f0-9])+)");
		Matcher matcher = pattern.matcher(output);
		TreeSet<String> emojis = new TreeSet<>(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				if (o1.length() > o2.length()) {
					return -1;
				}
				if (o1.length() < o2.length()) {
					return 1;
				}
				return o1.compareToIgnoreCase(o2);
			}
		});
		while (matcher.find()) {
			if (matcher.group(1).length() > 6) {
				emojis.add(matcher.group(1));
			}
		}
		String replacement;
		for (String emoji : emojis) {
			replacement = Integer.toHexString(StringEscapeUtils.unescapeJava(emoji).codePointAt(0));
			replacement = "<imgEmoji>U+" + replacement + "</imgEmoji>";
			while (output.contains(emoji)) {
				output = output.replace(emoji, replacement);
			}
		}
		return output;
	}
}
