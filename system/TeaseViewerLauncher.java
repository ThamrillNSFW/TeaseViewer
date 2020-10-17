package system;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.io.FileUtils;

import gui.TeaseViewerFrame;

public class TeaseViewerLauncher {

	public static void main(String[] args) {
		try {
			// Set cross-platform Java L&F (also called "Metal")
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (ClassNotFoundException e) {
			// handle exception
		} catch (InstantiationException e) {
			// handle exception
		} catch (IllegalAccessException e) {
			// handle exception
		}
		
		TeaseViewer tv = new TeaseViewer();
		
		File readmeFile = new File(tv.getApplicationFolder(), "readme.txt");
		if (!readmeFile.exists()) {
			try {
				URL inputUrl = ClassLoader.getSystemResource("resources/readme.txt");
				FileUtils.copyURLToFile(inputUrl, readmeFile);
			} catch (IOException e) {
				tv.getLogger().log(e);
			}
		}
		File infoFile=new File(tv.getDataFolder(), "info");
		try {
			URL url = new URL("https://raw.githubusercontent.com/Thamrill/TeaseViewer/main/resources/info");
			InputStream is=url.openConnection().getInputStream();
			FileOutputStream fos=new FileOutputStream(infoFile);
			fos.write(is.readAllBytes());
			fos.close();
			is.close();
		} catch (Exception e) {
			tv.getLogger().log(e);
			if(!infoFile.exists()) {
				try {
					URL inputUrl = ClassLoader.getSystemResource("resources/info");
					FileUtils.copyURLToFile(inputUrl, infoFile);
				} catch (IOException ex) {
					tv.getLogger().log(ex);
				}
			}
		}
		TeaseViewerFrame frame = new TeaseViewerFrame(tv);
		frame.setVisible(true);
	}

}
