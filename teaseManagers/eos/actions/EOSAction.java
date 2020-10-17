package teaseManagers.eos.actions;

import java.io.Serializable;

import org.json.simple.JSONObject;

import teaseManagers.EOSTeaseManager;
import teaseManagers.eos.EOSPageExecutor;

public abstract class EOSAction implements Serializable, Runnable{
	private static final long serialVersionUID = 1L;
	boolean enabled;
	String cmd;
	
	protected EOSTeaseManager teaseManager;
	protected EOSPageExecutor pageExecutor;
	public EOSAction(JSONObject parameters, EOSTeaseManager tm) {
		cmd=parameters.toJSONString();
	}
	
	public void setManager(EOSTeaseManager teaseManager) {
		this.teaseManager = teaseManager;
	}
	
	public void setPageExecutor(EOSPageExecutor pageExecutor) {
		this.pageExecutor = pageExecutor;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public EOSTeaseManager getManager() {
		return teaseManager;
	}

	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return cmd;
	}
}
