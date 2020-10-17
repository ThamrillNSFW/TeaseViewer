package teaseManagers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.TreeMap;

import org.graalvm.polyglot.HostAccess;

import javafx.application.Platform;
import javafx.scene.media.MediaView;
import system.TeaseViewer;
import teaseManagers.eos.AudioItem;
import teaseManagers.eos.actions.PlayAudioAction;
import utilities.FileUtilities;

public class AudioManagerFX {

	TreeMap<String, AudioItem> audioObjects;
	TeaseViewer app;
	ArrayList<AudioItem> others;

	public AudioManagerFX(TeaseViewer app) {
		this.app = app;
		audioObjects = new TreeMap<>();
		others = new ArrayList<>();
	}

	public void playAudioAction(PlayAudioAction playAudioAction, FileSystem fs) {
		String locator = playAudioAction.getLocator();
		String id = playAudioAction.getId();
		String loc = null;
		if (locator.startsWith("file:")) {
			loc = "media/" + locator.replaceFirst("file\\:", "");
		}
		File f = new File(app.getTempFolder(), id + "." + FileUtilities.getFileExtension(locator));
		try {
			f.createNewFile();
			try (InputStream is = Files.newInputStream(fs.getPath("/" + loc), StandardOpenOption.READ);
					FileOutputStream fos = new FileOutputStream(f)) {
				byte[] read = is.readAllBytes();
				fos.write(read);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		AudioItem audioItem = new AudioItem(id, loc, this);
		audioItem.setLoops(playAudioAction.getLoops());
		audioItem.setVolume(playAudioAction.getVolume());
		audioItem.setBackground(playAudioAction.isBackground());
		if (id != null) {
			audioObjects.put(id, audioItem);
		} else {
			others.add(audioItem);
		}

		MediaView mv = audioItem.getMediaPlayer();
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				app.getFrame().putMediaView(mv);
			}
		});
		audioItem.play();

	}

	public void clearAudios(boolean clearBackgrounds) {
		ArrayList<AudioItem> stopped = new ArrayList<>();
		for (AudioItem aO : others) {
			if (clearBackgrounds) {
				aO.stop();
				stopped.add(aO);
			} else {
				if (!aO.isBackground()) {
					aO.stop();
					stopped.add(aO);
				}
			}

		}
		others.removeAll(stopped);
		for (String key : audioObjects.keySet()) {
			AudioItem aO = audioObjects.get(key);
			if (clearBackgrounds) {
				aO.stop();
				audioObjects.remove(key);
			} else {
				if (!aO.isBackground()) {
					aO.stop();
					audioObjects.remove(key);
				}
			}
		}
	}

	@HostAccess.Export
	public AudioItem get(String key) {
		return audioObjects.get(key);
	}

	public TeaseViewer getApp() {
		return app;
	}

	public void remove(String key) {

	}

}