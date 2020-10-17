package teaseManagers;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.commons.text.StringEscapeUtils;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import gui.EOSPanel;
import gui.components.NotificationPanel;
import gui.dialogs.EndDialog;
import system.Logger;
import system.TeaseManager;
import system.TeaseViewer;
import teaseManagers.eos.EOSPageExecutor;
import teaseManagers.eos.actions.ChoiceAction;
import teaseManagers.eos.actions.CreateNotificationAction;
import teaseManagers.eos.actions.PromptAction;
import teaseManagers.eos.actions.SayAction;
import teaseManagers.eos.actions.TimerAction;

public class EOSTeaseManager extends TeaseManager {

	EOSPanel eosPanel;
	TeaseViewer app;
	Context context;
	PagesManager pagesManager;
	TeaseStorage teaseStorage;
	AudioManagerFX audioManager;
//	AudioManager audioManager;
	NotificationManager notificationManager;
	String initScript = "";
	TreeMap<String, String> teaseData;
	EOSPageExecutor executor;
	FileSystem fs;

	public EOSTeaseManager(TeaseViewer app) {
		this.app = app;
		eosPanel = new EOSPanel(this);
		context = Context.create("js");
		pagesManager = new PagesManager(app) {
			@Override
			public void pageChanged() {
				super.pageChanged();
				updateStatus();
			}
		};
		teaseStorage = new TeaseStorage(app);
//		audioManager = new AudioManager(app);
		audioManager = new AudioManagerFX(app);
		notificationManager = new NotificationManager(app, this);
		context.getBindings("js").putMember("console", new Console());
		context.getBindings("js").putMember("pages", pagesManager);
		context.getBindings("js").putMember("teaseStorage", teaseStorage);
		context.getBindings("js").putMember("Sound", audioManager);
		context.getBindings("js").putMember("Notification", notificationManager);

	}

	public TeaseViewer getApp() {
		return app;
	}

	@Override
	public JPanel getPanel() {
		return eosPanel;
	}

	public static class Console {
		public void log(String msg, Object[] vars) {
			String txt="console.js\t"+String.format(msg, vars);
			Logger.staticLog(txt, Logger.INFORMATION);
		}
	}

	String script;

	@Override
	public void setScript(String script) throws TeaseException {
		JSONParser parser = new JSONParser();

		try {
			JSONObject obj = (JSONObject) parser.parse(script);
			if (obj.containsKey("pages")) {
				pagesManager.setPages((JSONObject) obj.get("pages"));
			} else {
				throw new EOSLoadingException("No 'pages' found in script");
			}

			if (obj.containsKey("init")) {
				initScript = (String) obj.get("init");
			}
		} catch (ParseException pe) {
			throw new EOSLoadingException(pe.getMessage());
		}
	}

	public class EOSLoadingException extends TeaseException {
		private static final long serialVersionUID = 1L;

		public EOSLoadingException(String cause) {
			super(cause);
		}
	}

	@Override
	protected void initialize() {
		context.eval("js", initScript);
	}

	public void updateStatus() {
		status = new TreeMap<>();
		status.put("currentPage", pagesManager.getCurrentPageID());
		status.put("engineVariables", getEngineVariables());
		status.put("teaseStorage", teaseStorage.getStorage());
		status.put("teaseID", teaseData.get("teaseID"));
		status.put("teaseTitle", teaseData.get("teaseTitle"));
	}

	Boolean confirm = null;

	public void loadState(TreeMap<String, Serializable> state) {
		if (!((String) state.get("teaseID")).equalsIgnoreCase(teaseData.get("teaseID"))) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						int choice = JOptionPane.showConfirmDialog(app.getFrame(),
								"State ID and tease ID mismatch, do you confirm loading?\nID:" + state.get("teaseID")
										+ "/" + teaseData.get("teaseID"),
								"Confirm loading?", JOptionPane.YES_NO_OPTION);
						confirm = (choice == JOptionPane.NO_OPTION);
					}
				});
			} catch (InterruptedException | InvocationTargetException e) {
				Logger.staticLog(e);
			}
		}
		if (confirm) {
			eosPageExecutor.input("kill", null);
		}

	}

	public PagesManager getPagesManager() {
		return pagesManager;
	}

	public Value eval(String str) {
		String modifiedCMD = str.replaceAll("pages.goto\\('", "pages.goTo\\('");
		modifiedCMD = StringEscapeUtils.unescapeHtml3(modifiedCMD);
		return context.eval("js", modifiedCMD);
	}

	public AudioManagerFX getAudioManager() {
		return audioManager;
	}

	public NotificationManager getNotificationManager() {
		return notificationManager;
	}

	public void end(boolean compact) {
		if (!compact) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					flag = false;
					new EndDialog(SwingUtilities.getWindowAncestor(eosPanel), teaseData, app);
				}
			});
		}

		releaseRecources();
	}

	public void releaseRecources() {
		try {
			fs.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setFile(File f) {
		teaseFile = f;
		try {
			fs = FileSystems.newFileSystem(teaseFile.toPath(), new HashMap<>(), null);
		} catch (Exception e) {
			Logger.staticLog(e);
		}
	}

	@Override
	public void setTeaseData(TreeMap<String, String> teaseData) {
		this.teaseData = teaseData;
	}

	boolean flag;
	EOSPageExecutor eosPageExecutor;

	@Override
	public void run() {
		eval(initScript);
		pagesManager.goTo("start");

		flag = true;
		do {
			eosPageExecutor = new EOSPageExecutor(pagesManager.getActionQueue(), this) {
				@Override
				public void onPageCompletion() {
					eosPanel.reset();
					EOSTeaseManager.this.onPageCompletion();
				}
			};
			eosPageExecutor.run();
		} while (flag);
	}

	public void onPageCompletion() {
		audioManager.clearAudios(false);
	}

	public void setImage(String locator) {
		String loc = null;
		if (locator.startsWith("file:")) {
			loc = "media/" + locator.replaceFirst("file\\:", "");
		} else if (locator.startsWith("gallery:")) {
			loc = "media/" + locator.replaceFirst("gallery\\:", "") + ".jpg";
		}
		if (locator.contains("*")) {
			ArrayList<String> fileList = new ArrayList<>();
			String[] strs = locator.split("/");
			String folder = strs[0];
			if (folder.startsWith("file:")) {
				folder = "media/";
			} else if (folder.startsWith("gallery:")) {
				folder = "media/" + folder.replaceFirst("gallery\\:", "");
			}
			Pattern pattern = Pattern.compile(strs[1].replaceAll("\\*", ".*"));
			Matcher mat;
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(fs.getPath(folder))) {
				for (Path path : stream) {
					if (!Files.isDirectory(path)) {
						mat = pattern.matcher(path.getFileName().toString());
						if (mat.find()) {
							fileList.add(folder + "/" + path.getFileName().toString());
						}
					}
				}
				Random r = new Random();
				try (InputStream isr = Files.newInputStream(fs.getPath(fileList.get(r.nextInt(fileList.size()))))) {
					Image image = ImageIO.read(isr);
					eosPanel.setImage(image);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				Logger.staticLog(e);
			}
		} else {

			try (InputStream isr = Files.newInputStream(fs.getPath(loc))) {
				Image image = ImageIO.read(isr);
				eosPanel.setImage(image);
			} catch (Exception e) {
				Logger.staticLog(e);
			}
		}

	}

	public void createSayBubble(SayAction action) {
		eosPanel.createSayBubble(action);
	}

	public void createChoiceBubble(ChoiceAction action) {
		eosPanel.createChoiceBubble(action);
	}

	public void reportInput(String string, Object... parameters) {
		eosPageExecutor.input(string, parameters);
	}

	public void reportError(String string) {
		app.reportError(string);
	}

	public void createPromptBubble(PromptAction promptAction) {
		eosPanel.createPromptBubble(promptAction);
	}

	public TreeMap<String, Serializable> getEngineVariables() {
		TreeMap<String, Serializable> variables = new TreeMap<>();
		for (String key : context.getBindings("js").getMemberKeys()) {
			if (key.equalsIgnoreCase("pages") || key.equalsIgnoreCase("teaseStorage")
					|| key.equalsIgnoreCase("Notification") || key.equalsIgnoreCase("Sound")
					|| key.equalsIgnoreCase("console")) {
				continue;
			}
			Value val = context.getBindings("js").getMember(key);
			if (val.isString()) {
				variables.put(key, context.getBindings("js").getMember(key).asString());
			}
			if (val.isNumber()) {
				variables.put(key, context.getBindings("js").getMember(key).asDouble());
			}
			if (val.isBoolean()) {
				variables.put(key, context.getBindings("js").getMember(key).asBoolean());
			}

		}

		return variables;
	}

	public void createTimer(TimerAction timerAction) {
		eosPanel.createTimer(timerAction);
	}

	public FileSystem getFs() {
		return fs;
	}

	public NotificationPanel createNotificationAction(CreateNotificationAction createNotificationAction) {
		return eosPanel.createNotificationAction(createNotificationAction);
	}

	public EOSPageExecutor getEosPageExecutor() {
		return eosPageExecutor;
	}

	public void unfocus() {
		eosPanel.getBubblePanel().unfocus();
	}

}
