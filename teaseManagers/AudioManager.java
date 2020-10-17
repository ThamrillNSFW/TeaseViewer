package teaseManagers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.graalvm.polyglot.HostAccess;

import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import system.TeaseViewer;
import teaseManagers.eos.actions.PlayAudioAction;
import utilities.BasicPlayerAdapter;
import utilities.FileUtilities;

public class AudioManager {

	TreeMap<String, AudioObject> audioObjects;
	TeaseViewer app;
	ArrayList<AudioObject> others;

	public AudioManager(TeaseViewer app) {
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
		File f = new File(app.getTempFolder(), id+"."+FileUtilities.getFileExtension(locator));
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

		AudioObject audioObject = new AudioObject(id, loc, fs, this);
		audioObject.setLoops(playAudioAction.getLoops());
		audioObject.setVolume(playAudioAction.getVolume());
		audioObject.setBackground(playAudioAction.isBackground());
		audioObjects.put(id, audioObject);
		Thread t = new Thread(audioObject);
		t.start();

	}

	public void clearAudios(boolean clearBackgrounds) {
		ArrayList<AudioObject> stopped = new ArrayList<>();
		for (AudioObject aO : others) {
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
			AudioObject aO = audioObjects.get(key);
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
	public AudioObject get(String key) {
		return audioObjects.get(key);
	}

	public static class AudioObject implements Runnable {

		boolean background;
		Path path;
		Number volume;
		int loops;
		boolean finished;
		String fileName;
		FileSystem fs;
		String key;
		AudioManager manager;
		BasicPlayer player;
		Map<?, ?> audioInfo = null;

		public AudioObject(String key, String fileName, FileSystem fs, AudioManager manager) {
			this.fs = fs;
			this.key = key;
			this.fileName = fileName;
		}

		@Override
		public void run() {
			try {
				InputStream is;
				do {
					finished = false;
					is = Files.newInputStream(fs.getPath("/" + fileName), StandardOpenOption.READ);
					BufferedInputStream bis = new BufferedInputStream(is);
					player = new BasicPlayer();
					try {
						player.addBasicPlayerListener(new BasicPlayerAdapter() {
							@Override
							public void stateUpdated(BasicPlayerEvent arg0) {
								if (arg0.getCode() == BasicPlayerEvent.EOM) {
									finished = true;
								}
								if (arg0.getCode() == BasicPlayerEvent.STOPPED) {
									finished = true;
								}
							}

							@Override
							public void opened(Object arg0, Map arg1) {
								audioInfo = arg1;
							}
						});
						player.open(bis);
						player.play();
						while (!finished) {
							try {
								Thread.sleep(20);
							} catch (InterruptedException ie) {
								ie.printStackTrace();
							}
						}
						player.stop();
						bis.close();
						is.close();
					} catch (BasicPlayerException e) {
						e.printStackTrace();
					}
					loops--;
				} while (loops != 0 && !stopped);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void setBackground(boolean background) {
			this.background = background;
		}

		public boolean isBackground() {
			return background;
		}

		public void setVolume(Number volume) {
			this.volume = volume;
		}

		public Number getVolume() {
			return volume;
		}

		public void setLoops(int loops) {
			this.loops = loops;
		}

		public int getLoops() {
			return loops;
		}

		public void play() {
			if (player != null) {
				try {
					player.play();
				} catch (BasicPlayerException e) {
					e.printStackTrace();
				}
			}
		}

		public void pause() {
			if (player != null) {
				try {
					player.pause();
				} catch (BasicPlayerException e) {
					e.printStackTrace();
				}
			}
		}

		boolean stopped = false;

		@HostAccess.Export
		public void stop() {
			stopped = true;
			if (player != null) {
				try {
					player.stop();
				} catch (BasicPlayerException e) {
					e.printStackTrace();
				}
			}
		}

		public void seek(Number time) {

			String type = (String) audioInfo.get("audio.type");
			if ((type.equalsIgnoreCase("mp3")) && (audioInfo.containsKey("audio.length.bytes"))
					&& (audioInfo.containsKey("duration"))) {
				long lenghtInSeconds = ((Long) audioInfo.get("duration")).longValue() / 1000000;
				double fraction = time.doubleValue() * 1.0 / lenghtInSeconds;
				long skipBytes = (long) Math
						.round(((Integer) audioInfo.get("audio.length.bytes")).intValue() * fraction);
				try {
					player.seek(skipBytes);
				} catch (BasicPlayerException e) {
					e.printStackTrace();
				}
			}
		}

		public void destroy() {
			stop();
			manager.audioObjects.remove(key);
		}

	}

	public TeaseViewer getApp() {
		return app;
	}

	public void remove(String key) {
		
	}

}