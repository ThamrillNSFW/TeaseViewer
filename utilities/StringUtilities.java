package utilities;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.text.StringEscapeUtils;
import org.graalvm.polyglot.Value;

import teaseManagers.EOSTeaseManager;

public class StringUtilities {

	public static String convertStringForDisplay(String input, String emojiSet, EOSTeaseManager etm) {
		String toReturn = StringEscapeUtils.unescapeJava(ScriptConverter.convertScript(input));
		Pattern pattern = Pattern.compile("<imgEmoji>(.+?)</imgEmoji>");
		Matcher matcher = pattern.matcher(toReturn);
		while (matcher.find()) {
			String str = "/resources/emoji/" + emojiSet + "/" + matcher.group(1).toUpperCase() + ".png";
			URL res = StringUtilities.class.getResource(str);
			if (res == null) {
				str = "/resources/emoji/" + emojiSet + "/" + matcher.group(1).toUpperCase() + "U+FE0F.png";
				res = StringUtilities.class.getResource(
						str);
			}
			String img;
			int size=(int)(etm.getApp().getParameters().get("defaultFontSize"));
			if (res == null) {
				img = "!Missing emoji reference!";
			} else {
				img = "<img width='"+size+"' height='"+size+"' src=\"" + res + "\">";
			}

			toReturn = matcher.replaceFirst(img);
			matcher = pattern.matcher(toReturn);
		}

		pattern = Pattern.compile("<eval>(.+?)</eval>");
		matcher = pattern.matcher(toReturn);
		Value val;
		while (matcher.find()) {
			String replacementValue = "!missing eval!";

			val = etm.eval(matcher.group(1));
			replacementValue = val.toString();

			toReturn = matcher.replaceFirst(replacementValue);
			matcher = pattern.matcher(toReturn);
		}

		return toReturn;
	}

}
