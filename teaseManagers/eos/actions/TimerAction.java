package teaseManagers.eos.actions;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import teaseManagers.EOSTeaseManager;
import teaseManagers.eos.ActionFactory;

public class TimerAction extends EOSAction {
	private static final long serialVersionUID = 1L;
	String duration;
	String style;
	boolean isAsync;
	ArrayList<EOSAction> commands;

	public TimerAction(JSONObject parameters, EOSTeaseManager tm) {
		super(parameters, tm);
		style="normal";
		for (Object o : parameters.keySet()) {
			switch ((String) o) {
			case "duration":
				duration = (String) parameters.get((String) o);
				break;
			case "style":
				style = (String) parameters.get((String) o);
				break;
			case "isAsync":
				isAsync = (Boolean) parameters.get((String) o);
				break;
			case "commands":
				commands = new ArrayList<EOSAction>();
				JSONArray array = (JSONArray) parameters.get("commands");
				if (array != null) {
					for (Object o1 : array) {
						commands.add(ActionFactory.createAction((JSONObject) o1, tm));
					}
				}
				break;
			}
		}
	}

	public boolean isAsync() {
		return isAsync;
	}

	@Override
	public void run() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				teaseManager.createTimer(TimerAction.this);
			}
		});
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		while (!isAsync()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (pageExecutor.getInputs().containsKey("timer")) {
				break;
			}
		}
	}

	public static long parseDuration(String duration) {
		Pattern pattern = Pattern.compile("(\\d+)(\\w)");
		Matcher matcher = pattern.matcher(duration);
		long val=60;
		if(matcher.find()) {
			switch(matcher.group(2).toLowerCase()) {
			case "s":
				val=Integer.parseInt(matcher.group(1));
				break;
			case "m":
				val=Integer.parseInt(matcher.group(1))*60;
				break;
			case "h":
				val=Integer.parseInt(matcher.group(1))*3600;
				break;
			}
		}
		return val;
	}

	public long getSeconds() {
		return parseDuration(duration);
	}
	
	public String getStyle() {
		return style;
	}
	
	public ArrayList<EOSAction> getCommands() {
		return commands;
	}
}
