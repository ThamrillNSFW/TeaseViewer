package system;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

	public static Logger instance;
	public int loggerLevel;
	public int consoleLevel;
	File logFile;
	TeaseViewer app;
	

	public static final int INFORMATION = 0;
	public static final int WARNING = INFORMATION + 1;
	public static final int ERROR = WARNING + 1;

	public File getLogFile() {
		return logFile;
	}

	public void initialize(TeaseViewer app) {
		File folder = new File(app.getApplicationFolder(), "logs");
		this.app=app;
		folder.mkdir();
		do {
			logFile = new File(folder, new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".log");
		} while (logFile.exists());
		try {
			logFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		updateLoggerParameters();
	}

	public void log(String str, int severity) {
		if (severity >= consoleLevel) {
			System.out.println(str);
		}
		if (severity >= loggerLevel) {
			try (FileWriter fw = new FileWriter(logFile, true); BufferedWriter bw = new BufferedWriter(fw)) {
				bw.write("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "]\t" + str);
				bw.newLine();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void log(Exception exc) {
		exc.printStackTrace(System.err);
		try (FileWriter fw = new FileWriter(logFile, true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter pw = new PrintWriter(bw)) {
			exc.printStackTrace(pw);
			bw.newLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void staticLog(String str, int severity) {
		instance.log(str, severity);
	}

	public static void staticLog(Exception exc) {
		instance.log(exc);
	}

	public static void setInstance(Logger instance) {
		Logger.instance = instance;
	}

	public static void updateParameters() {
		Logger.instance.updateLoggerParameters();
	}

	public void updateLoggerParameters() {
		loggerLevel=(int) app.getParameters().get("loggerPriority");
		consoleLevel=(int) app.getParameters().get("consolePriority");
	}
}
