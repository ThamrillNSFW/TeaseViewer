package system;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.TreeMap;

import javax.swing.JPanel;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import teaseManagers.EOSTeaseManager;
import utilities.Encryptor;
import utilities.ScriptConverter;

public abstract class TeaseManager implements Runnable {

	protected File teaseFile;
	protected TreeMap<String, Serializable> status;

	@SuppressWarnings("unchecked")
	public static TeaseManager createTeaseManager(File f, TeaseViewer app) {
		Path path = f.toPath();
		String key = null;
		String script = null;
		TreeMap<String, String> teaseData = null;
		try (FileSystem fs = FileSystems.newFileSystem(path, new HashMap<>(), null)) {
			try (InputStream isr = Files.newInputStream(fs.getPath("tease.data"));
					ObjectInputStream ois = new ObjectInputStream(isr)) {
				teaseData = (TreeMap<String, String>) ois.readObject();

			} catch (IOException e) {
				e.printStackTrace();
			}
			boolean nyx = Boolean.parseBoolean(teaseData.get("nyx"));
			if (nyx) {
				try (BufferedReader reader = Files.newBufferedReader(fs.getPath("script.json"),
						Charset.forName("UTF-8"))) {
					script = reader.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				script = "";
				try (BufferedReader br = Files.newBufferedReader(fs.getPath("script.json"))) {
					String read = br.readLine();
					while (read != null) {
						script += read + "\r\n";
						read = br.readLine();
					}
					script=ScriptConverter.convertScript(script);
				} catch (IOException e) {
				}
				if (script.isBlank()) {
					key = Integer.toHexString(teaseData.hashCode());
					if (key == null) {
						return null; // TODO
					}
					try (InputStream isr = Files.newInputStream(fs.getPath("script.json"));
							BufferedInputStream bis = new BufferedInputStream(isr)) {
						byte[] bytes = bis.readAllBytes();
						script = Encryptor.decryptString(bytes, key);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				} else {
					try {
						new JSONParser().parse(script);
					} catch (Exception e) {
						key = Integer.toHexString(teaseData.hashCode());
						if (key == null) {
							return null; // TODO
						}
						try (InputStream isr = Files.newInputStream(fs.getPath("script.json"));
								BufferedInputStream bis = new BufferedInputStream(isr)) {
							byte[] bytes = bis.readAllBytes();
							script = Encryptor.decryptString(bytes, key);
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					}
				}

				EOSTeaseManager etm = new EOSTeaseManager(app);
				etm.setFile(f);
				etm.setScript(script);
				etm.setTeaseData(teaseData);
				return etm;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public abstract void setFile(File f);

	public abstract void setTeaseData(TreeMap<String, String> teaseData);

	public abstract void setScript(String script) throws Exception;

	public abstract JPanel getPanel();

	protected abstract void initialize();

	public TreeMap<String, Serializable> getState() {
		return status;
	}

	public File getTeaseFile() {
		return teaseFile;
	}

	public void end(boolean compact) {
		releaseRecources();
	}

	public void releaseRecources() {
	}

}
