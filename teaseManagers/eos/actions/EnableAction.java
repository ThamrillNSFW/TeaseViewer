package teaseManagers.eos.actions;

import org.json.simple.JSONObject;

import teaseManagers.EOSTeaseManager;

public class EnableAction extends EOSAction{
	private static final long serialVersionUID = 1L;
	String target;

	public EnableAction(JSONObject parameters, EOSTeaseManager tm) {
		super(parameters, tm);
		target = (String) parameters.get("target");
	}
	
	public String getTarget() {
		return target;
	}
	
	@Override
	public void run() {
		teaseManager.getPagesManager().enable(getTarget());
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
