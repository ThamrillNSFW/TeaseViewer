package utilities;

import java.io.File;
import java.io.FileWriter;


public class LinkUtility {
	public static void createInternetShortcut(File where, String target, String icon){
		try (FileWriter fw = new FileWriter(where)){
			fw.write("[InternetShortcut]\n");
			fw.write("URL=" + target + "\n");
			if (!icon.equals("")) {
				fw.write("IconFile=" + icon + "\n");
			}
			fw.flush();
			fw.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
