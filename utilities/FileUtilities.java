package utilities;

import java.io.File;

public class FileUtilities {
	public static boolean deleteDirectory(File directoryToBeDeleted) {
		File[] allContents = directoryToBeDeleted.listFiles();
		if (allContents != null) {
			for (File file : allContents) {
				deleteDirectory(file);
			}
		}
		return directoryToBeDeleted.delete();
	}

	public static String getFileExtension(File f) {
		return getFileExtension(f.getName());
	}

	public static String getFileExtension(String name) {
		if(name.contains(".")&&name.lastIndexOf(".")>0) {
			return name.substring(name.lastIndexOf(".")+1);
			
		}
		return null;
	}
}
