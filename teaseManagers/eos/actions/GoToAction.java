package teaseManagers.eos.actions;

import org.json.simple.JSONObject;

import teaseManagers.EOSTeaseManager;

public class GoToAction extends EOSAction {
	private static final long serialVersionUID = 1L;
	String target;
	
	public GoToAction(JSONObject parameters, EOSTeaseManager tm) {
		super(parameters, tm);
		target=(String) parameters.get("target");
	}
	
	public String getTarget() {
		return target;
	}

	@Override
	public void run() {
		teaseManager.getPagesManager().goTo(getTarget());
		pageExecutor.setFlag(false);
	}

}
