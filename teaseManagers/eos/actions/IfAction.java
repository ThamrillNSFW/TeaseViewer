package teaseManagers.eos.actions;

import java.util.ArrayList;
import java.util.LinkedList;

import org.graalvm.polyglot.Value;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import teaseManagers.EOSTeaseManager;
import teaseManagers.eos.ActionFactory;


public class IfAction extends EOSAction {
	private static final long serialVersionUID = 1L;

	String condition;
	ArrayList<EOSAction> commands;
	ArrayList<EOSAction> elseCommands;

	public IfAction(JSONObject parameters, EOSTeaseManager tm) {
		super(parameters, tm);
		condition = (String) parameters.get("condition");
		commands = new ArrayList<EOSAction>();
		JSONArray array = (JSONArray) parameters.get("commands");
		if (array != null) {
			for (Object o1 : array) {
					commands.add(ActionFactory.createAction((JSONObject)o1, tm));
			}
		}
		elseCommands = new ArrayList<EOSAction>();
		array = (JSONArray) parameters.get("elseCommands");
		if (array != null) {
			for (Object o1 : array) {
					commands.add(ActionFactory.createAction((JSONObject)o1, tm));
			}
		}
	}
	
	public String getCondition() {
		return condition;
	}
	
	public ArrayList<EOSAction> getCommands() {
		return commands;
	}
	
	public ArrayList<EOSAction> getElseCommands() {
		return elseCommands;
	}
	
	@Override
	public void run() {
		Value bool = teaseManager.eval(getCondition());
		if (bool.asBoolean()) {
			pageExecutor.injectCommands(getCommands());
		} else {
			pageExecutor.injectCommands(getElseCommands());
		}
//		pageExecutor.getActions().addAll(tempActions);
	}

}
