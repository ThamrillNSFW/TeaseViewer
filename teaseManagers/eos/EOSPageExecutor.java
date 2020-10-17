package teaseManagers.eos;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeMap;

import javax.swing.SwingUtilities;

import org.json.simple.JSONObject;

import system.Logger;
import teaseManagers.EOSTeaseManager;
import teaseManagers.eos.actions.EOSAction;

public class EOSPageExecutor implements Runnable {
	EOSTeaseManager teaseManager;
	LinkedList<EOSAction> actions;

	public EOSPageExecutor(LinkedList<JSONObject> actionQueue, EOSTeaseManager tm) {
		teaseManager = tm;
		actions = new LinkedList<>();
		for (JSONObject action : actionQueue) {
			EOSAction eosAction=ActionFactory.createAction(action, tm);
			if(eosAction!=null) {
				actions.add(eosAction);
			}
			
		}
	}

	public LinkedList<EOSAction> getActions() {
		return actions;
	}

	public void setActions(LinkedList<EOSAction> actions) {
		this.actions = actions;
	}

	boolean flag;

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	@Override
	public void run() {
		EOSAction action;
		flag = true;
		while (flag) {
			inputs = new TreeMap<>();
			while (actions.isEmpty()) {
				if (!flag) {
					break;
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (!actions.isEmpty()) {
				action = actions.pop();
				if(action!=null) {
					Logger.staticLog(action.getClass()+":"+action, Logger.INFORMATION);
					action.setManager(teaseManager);
					action.setPageExecutor(this);
					action.run();
				}else {
					SwingUtilities.invokeLater(new Runnable() {
						
						@Override
						public void run() {
							teaseManager.reportError("null action");
						}
					});
					teaseManager.end(false);
					flag=false;
				}
			}
		}
		onPageCompletion();
	}

	public void onPageCompletion() {
		Logger.staticLog("Page completed", Logger.INFORMATION);
	}

	public TreeMap<String, Object[]> getInputs() {
		return inputs;
	}

	TreeMap<String, Object[]> inputs;

	public void input(String type, Object[] parameters) {
		inputs.put(type, parameters);
	}

	public void injectCommands(ArrayList<EOSAction> commands) {
		if(commands==null) {
			return;
		}
		LinkedList<EOSAction> tempActions = new LinkedList<>(actions);
		actions = new LinkedList<>();
		actions.addAll(commands);
		actions.addAll(tempActions);
	}

}
