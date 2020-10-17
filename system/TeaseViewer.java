package system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FileUtils;

import gui.TeaseViewerFrame;
import teaseManagers.EOSTeaseManager;
import utilities.FileUtilities;

public class TeaseViewer {
	TreeMap<String, Object> parameters;
	IconManager iconManager;
	TeaseManager teaseManager;
	Logger logger;
	TeaseViewerFrame tvf;
	static final String version = "pre-a_0.0.0";
	File tempFolder;

	public TeaseViewerFrame getFrame() {
		return tvf;
	}

	public static String getVersion() {
		return version;
	}

	@SuppressWarnings("unchecked")
	public TeaseViewer() {
		parameters = new TreeMap<>();
		parameters.put("emojiSet", "OpenMoji");
		parameters.put("loggerPriority", 1);
		parameters.put("consolePriority", 2);
		parameters.put("defaultFontSize", 17f);

		logger = new Logger();
		logger.initialize(this);
		Logger.setInstance(logger);

		iconManager = new IconManager();
		iconManager.initialize();
		File f = new File(getApplicationFolder(), "preferences");
		try (FileInputStream fis = new FileInputStream(f); ObjectInputStream ois = new ObjectInputStream(fis)) {
			parameters = (TreeMap<String, Object>) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		parameters.put("encrypt", false);
		logger.updateLoggerParameters();
		String tempFolderName = "temp" + String.format("%06X", (int) (Math.random() * (16777216)));
		while (new File(getDataFolder(), tempFolderName).exists()) {
			tempFolderName = "temp" + String.format("%06X", (int) (Math.random() * (16777216)));
		}
		tempFolder = new File(getDataFolder(), tempFolderName);
		tempFolder.mkdir();
	}

	public TreeMap<String, Object> getParameters() {
		return parameters;
	}

	public ImageIcon getIcon(String key) {
		return iconManager.getIcon(key);
	}

	File applicationFolder;

	public File getApplicationFolder() {
		if (applicationFolder == null) {
			try {
				applicationFolder= new File(TeaseViewer.class.getProtectionDomain().getCodeSource().getLocation().toURI())
						.getParentFile();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return applicationFolder;
	}
	
	File dataFolder;
	
	public File getDataFolder() {
		if(dataFolder==null) {
			File appFolder=getApplicationFolder();
			if(appFolder!=null) {
				dataFolder=new File(appFolder, "data");
				if(!dataFolder.exists()) {
					dataFolder.mkdir();
				}
			}
		}
		return dataFolder;
		
	}

	public File getTempFolder() {
		return tempFolder;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setTeaseViewerFrame(TeaseViewerFrame tvf) {
		this.tvf = tvf;
	}

	public TeaseManager getTeaseManager() {
		return teaseManager;
	}

	public void createTeaseManager(File selectedFile) {
		if (this.teaseManager != null) {
			this.teaseManager.end(true);
		}
		TeaseManager teaseManager = TeaseManager.createTeaseManager(selectedFile, this);
		if (teaseManager != null) {
			this.teaseManager = teaseManager;
			tvf.setPanel(teaseManager.getPanel());
			this.teaseManager.initialize();
		}
		if (this.teaseManager instanceof EOSTeaseManager) {
			Thread t = new Thread(this.teaseManager);
			t.start();
		}
	}

	File lastSaveFile;

	public File getLastSaveFile() {
		return lastSaveFile;
	}

	public void saveState(File saveFile) {
		if (saveFile.getName().endsWith(".tease")) {
			Map<String, String> env = new HashMap<>();
			env.put("create", "true");
			Path path = Paths.get(saveFile.toURI());
			URI uri = URI.create("jar:" + path.toUri());
			try (FileSystem fs = FileSystems.newFileSystem(uri, env)) {
				Path nf = fs.getPath("autosave.teaseState");
				try (OutputStream os = Files.newOutputStream(nf, StandardOpenOption.CREATE);
						ObjectOutputStream oos = new ObjectOutputStream(os)) {
					oos.writeObject(teaseManager.getState());
				} catch (Exception e) {
					logger.log(e);
					return;
				}
			} catch (Exception e) {
				logger.log(e);
				return;
			}
			lastSaveFile = saveFile;
		} else if (saveFile.getName().endsWith(".teaseState")) {
			try (FileOutputStream os = new FileOutputStream(saveFile);
					ObjectOutputStream oos = new ObjectOutputStream(os)) {
				oos.writeObject(teaseManager.getState());
			} catch (Exception e) {
				logger.log(e);
				return;
			}
			lastSaveFile = saveFile;
		} else {
			File temp = new File(saveFile.getParentFile(), saveFile.getName() + ".teaseState");
			saveState(temp);
		}

	}

	@SuppressWarnings("unchecked")
	public TreeMap<String, Serializable> loadState(File loadFile) {
		TreeMap<String, Serializable> state = new TreeMap<>();
		if (loadFile.getName().endsWith(".tease")) {
			Map<String, String> env = new HashMap<>();
			Path path = Paths.get(loadFile.toURI());
			URI uri = URI.create("jar:" + path.toUri());
			try (FileSystem fs = FileSystems.newFileSystem(uri, env)) {
				Path nf = fs.getPath("autosave.teaseState");
				try (InputStream is = Files.newInputStream(nf, StandardOpenOption.CREATE);
						ObjectInputStream ois = new ObjectInputStream(is)) {
					state = (TreeMap<String, Serializable>) ois.readObject();
				} catch (Exception e) {
					logger.log(e);
				}
			} catch (Exception e) {
				logger.log(e);
			}
		} else {
			try (FileInputStream fis = new FileInputStream(loadFile);
					ObjectInputStream ois = new ObjectInputStream(fis)) {
				state = (TreeMap<String, Serializable>) ois.readObject();
			} catch (Exception e) {
				logger.log(e);
			}
		}

		return state;
	}

	public void reportError(String string) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JOptionPane.showMessageDialog(tvf,
						"TeaseViewer encountered an error.\nInformation about error:" + string, "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		});
	}

	public void clearTempFolder(boolean clearAll) {
		if (clearAll) {
			File[] files = getDataFolder().listFiles();
			for (File f : files) {
				if (f.exists() && f.isDirectory() && f.getName().startsWith("temp")
						&& !f.getName().equalsIgnoreCase(tempFolder.getName())) {
					FileUtilities.deleteDirectory(f);
				}
			}
		} else {
			FileUtilities.deleteDirectory(tempFolder);
		}
	}
}
