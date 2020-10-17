package teaseManagers.eos.actions;

import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.SwingUtilities;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import gui.eospanel.bubbles.ButtonsBubble;
import teaseManagers.EOSTeaseManager;
import teaseManagers.eos.ActionFactory;

public class ChoiceAction extends EOSAction {
	private static final long serialVersionUID = 1L;

	ArrayList<ChoiceActionOption> options;

	public ChoiceAction(JSONObject parameters, EOSTeaseManager tm) {
		super(parameters, tm);
		options=new ArrayList<ChoiceAction.ChoiceActionOption>();
		for (Object o : parameters.keySet()) {
			switch ((String) o) {
			case "options":
				JSONArray array = (JSONArray) parameters.get(o);
				for (Object o1 : array) {
					options.add(new ChoiceActionOption((JSONObject) o1, tm));
				}
				break;
			}
		}
	}
	
	public ArrayList<ChoiceActionOption> getOptions() {
		return options;
	}

	public static class ChoiceActionOption {
		String visible;
		String color="#0A0A0A";
		String label;
		ArrayList<EOSAction> commands;

		public ChoiceActionOption(JSONObject option,  EOSTeaseManager tm) {
			commands=new ArrayList<EOSAction>();
			for (Object o : option.keySet()) {
				switch ((String) o) {
				case "label":
					label = (String) option.get((String) o);
					break;
				case "commands":
					JSONArray array = (JSONArray) option.get("commands");
					if (array != null) {
						for (Object o1 : array) {
								commands.add(ActionFactory.createAction((JSONObject)o1, tm));
						}
					}
					break;
				case "color":
					color = (String) option.get((String) o);
					break;
				case "visible":
					visible = (String) option.get((String) o);
					break;
				}
			}
		}
		
		public ArrayList<EOSAction> getCommands() {
			return commands;
		}
		
		public String getVisible() {
			return visible;
		}
		
		public String getColor() {
			return color;
		}
		
		public String getLabel() {
			return label;
		}
	}
	
	ButtonsBubble bubble;

	@Override
	public void run() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				teaseManager.createChoiceBubble(ChoiceAction.this);
			}
		});

		while (true) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (pageExecutor.getInputs().containsKey("notification")) {
				if(bubble!=null) {
					bubble.transform("...", true, true, ((Number) teaseManager.getApp().getParameters().get("defaultFontSize")).floatValue() );
				}
				break;
			}
			if (pageExecutor.getInputs().containsKey("timer")) {
				if(bubble!=null) {
					bubble.transform("...", true, true, ((Number) teaseManager.getApp().getParameters().get("defaultFontSize")).floatValue() );
				}
				break;
			}
			if (pageExecutor.getInputs().containsKey("choiceButton") && (pageExecutor.getInputs().get("choiceButton")[0] instanceof Integer)) {
				Integer index = (Integer) pageExecutor.getInputs().get("choiceButton")[0];
				LinkedList<EOSAction> tempActions = new LinkedList<>(pageExecutor.getActions());
				pageExecutor.setActions(new LinkedList<>(getOptions().get(index).getCommands()));
				pageExecutor.getActions().addAll(tempActions);
				break;
			}
		}
	}
	
	public void setBubble(ButtonsBubble bubble) {
		this.bubble = bubble;
	}

}
