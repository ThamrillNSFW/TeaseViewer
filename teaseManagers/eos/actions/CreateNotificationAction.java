package teaseManagers.eos.actions;

import java.util.ArrayList;

import javax.swing.SwingUtilities;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import teaseManagers.EOSTeaseManager;
import teaseManagers.eos.ActionFactory;

public class CreateNotificationAction extends EOSAction {
	private static final long serialVersionUID = 1L;
	String title;
	String buttonLabel;
	String timerDuration;
	String id;

	ArrayList<EOSAction> buttonCommands;
	ArrayList<EOSAction> timerCommands;

	public ArrayList<EOSAction> getButtonCommands() {
		return buttonCommands;
	}

	public ArrayList<EOSAction> getTimerCommands() {
		return timerCommands;
	}

	public String getId() {
		return id;
	}

	public String getButtonLabel() {
		return buttonLabel;
	}

	public String getLabel() {
		return title;
	}

	public Long getDuration() {
		if(timerDuration==null) {
			return null;
		}
		if (timerDuration.startsWith("$")) {
			return teaseManager.eval(timerDuration.substring(1)).asLong();
		} else {
			if (timerDuration.contains("-")) {
				String[] interval = timerDuration.split("-");
				Long from = TimerAction.parseDuration(interval[0]);
				Long to = TimerAction.parseDuration(interval[1]);
				if (to < from) {
					Long temp = to;
					to = from;
					from = temp;
				}
				return (long) (from + Math.random() * (to - from));
			} else {
				return TimerAction.parseDuration(timerDuration);
			}
		}
	}

	public CreateNotificationAction(JSONObject parameters, EOSTeaseManager tm) {
		super(parameters, tm);
		JSONArray array;
		this.teaseManager = tm;
		for (Object o : parameters.keySet()) {
			switch ((String) o) {
			case "title":
				title = (String) parameters.get((String) o);
				break;
			case "buttonLabel":
				buttonLabel = (String) parameters.get((String) o);
				break;
			case "timerDuration":
				timerDuration = (String) parameters.get((String) o);
				break;
			case "id":
				id = (String) parameters.get((String) o);
				break;
			case "buttonCommands":
				buttonCommands = new ArrayList<EOSAction>();
				array = (JSONArray) parameters.get("buttonCommands");
				if (array != null) {
					for (Object o1 : array) {
						buttonCommands.add(ActionFactory.createAction((JSONObject) o1, tm));
					}
				}
				break;
			case "timerCommands":
				timerCommands = new ArrayList<EOSAction>();
				array = (JSONArray) parameters.get("timerCommands");
				if (array != null) {
					for (Object o1 : array) {
						timerCommands.add(ActionFactory.createAction((JSONObject) o1, tm));
					}
				}
				break;
			}
		}
	}

	@Override
	public void run() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				teaseManager.getNotificationManager().createNotificationAction(CreateNotificationAction.this);
			}
		});
	}

}
