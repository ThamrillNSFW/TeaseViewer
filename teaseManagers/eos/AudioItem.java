package teaseManagers.eos;

import java.io.File;

import org.graalvm.polyglot.HostAccess;

import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import system.Logger;
import teaseManagers.AudioManagerFX;
import utilities.FileUtilities;

public class AudioItem {

	boolean background;
	Number volume;
	int loops;
	boolean finished;
	String fileName;
	String key;
	AudioManagerFX manager;
	MediaView mediaView;

	public AudioItem(String key, String fileName, AudioManagerFX audioManagerFX) {
		this.key = key;
		this.fileName = fileName;
		this.manager = audioManagerFX;
	}

	public MediaView getMediaPlayer() {
		MediaPlayer player = new MediaPlayer(new Media(
				new File(manager.getApp().getTempFolder(), key + "." + FileUtilities.getFileExtension(fileName)).toURI()
						.toString()));
		player.setOnError(() ->Logger.staticLog("Media error occurred: " + player.getError(), Logger.ERROR));
		MediaView mediaView = new MediaView(player);
		mediaView.setMediaPlayer(player);
		mediaView.getMediaPlayer().setCycleCount(loops == 0 ? MediaPlayer.INDEFINITE : loops);
		if(volume!=null) {
			mediaView.getMediaPlayer().setVolume(volume.doubleValue());
		}
		this.mediaView=mediaView;
		return mediaView;
	}

	public void setBackground(boolean background) {
		this.background = background;
	}

	public boolean isBackground() {
		return background;
	}
	@HostAccess.Export
	public void setVolume(Number volume) {
		this.volume = volume;
	}

	public Number getVolume() {
		return volume;
	}
	@HostAccess.Export
	public void setLoops(int loops) {
		this.loops = loops;
	}

	public int getLoops() {
		return loops;
	}

	@HostAccess.Export
	public void play() {
		if (mediaView == null) {
			mediaView = getMediaPlayer();
		}
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				mediaView.getMediaPlayer().play();
			}
		});
	}

	@HostAccess.Export
	public void pause() {
		if (mediaView != null) {
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					mediaView.getMediaPlayer().pause();
				}
			});
		}
	}

	@HostAccess.Export
	public void stop() {
		if (mediaView != null) {
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					mediaView.getMediaPlayer().stop();
				}
			});
		}
	}

	@HostAccess.Export
	public void seek(Number time) {
		if (mediaView != null) {
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					mediaView.getMediaPlayer().seek(new Duration(time.doubleValue()));
				}
			});
		}
	}

	@HostAccess.Export
	public void destroy() {
		stop();
		manager.remove(key);
	}
	
	public MediaView getMediaView() {
		return mediaView;
	}
}
