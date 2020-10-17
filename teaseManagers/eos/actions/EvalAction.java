package teaseManagers.eos.actions;

import javax.swing.SwingUtilities;

import org.json.simple.JSONObject;

import system.Logger;
import teaseManagers.EOSTeaseManager;

public class EvalAction extends EOSAction {
	private static final long serialVersionUID = 1L;
	String script;

	public EvalAction(JSONObject parameters, EOSTeaseManager tm) {
		super(parameters, tm);
		script = (String) parameters.get("script");
	}

	public String getScript() {
		return script;
	}

	@Override
	public void run() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				Logger.staticLog(getScript(), Logger.INFORMATION);

			}
		});
		teaseManager.eval(getScript());
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
