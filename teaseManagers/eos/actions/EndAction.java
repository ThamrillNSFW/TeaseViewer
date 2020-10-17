package teaseManagers.eos.actions;

import org.json.simple.JSONObject;

import teaseManagers.EOSTeaseManager;

public class EndAction extends EOSAction {
	private static final long serialVersionUID = 1L;
	
	public EndAction(JSONObject parameters, EOSTeaseManager tm) {
		super(parameters, tm);
	}

	@Override
	public void run() {
		teaseManager.end(false);
	}

}
