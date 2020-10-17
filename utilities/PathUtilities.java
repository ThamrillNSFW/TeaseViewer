package utilities;

public class PathUtilities {

	public static String makeCompliantPath(String path) {
		String compliantPath=path;
		compliantPath=compliantPath.replaceAll("/", "");
		compliantPath=compliantPath.replaceAll("<", "");
		compliantPath=compliantPath.replaceAll(">", "");
		compliantPath=compliantPath.replaceAll(":", "");
		compliantPath=compliantPath.replaceAll("\"", "");
		compliantPath=compliantPath.replaceAll("|", "");
		compliantPath=compliantPath.replaceAll("\\?", "");
		compliantPath=compliantPath.replaceAll("\\*", "");
		compliantPath=compliantPath.replaceAll("'", "");
		compliantPath=compliantPath.trim();
		return compliantPath;
	}

}
