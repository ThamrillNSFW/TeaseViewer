package teaseManagers.eos.actions;

import org.json.simple.JSONObject;

import teaseManagers.EOSTeaseManager;

public class DisableAction extends EOSAction{
	private static final long serialVersionUID = 1L;
	String target;
	
	public DisableAction(JSONObject parameters, EOSTeaseManager tm) {
		super(parameters, tm);
		target=(String) parameters.get("target");
	}
	
	public String getTarget() {
		return target;
	}

	@Override
	public void run() {
		teaseManager.getPagesManager().disable(getTarget());
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
