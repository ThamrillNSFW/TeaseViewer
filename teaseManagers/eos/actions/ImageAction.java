package teaseManagers.eos.actions;

import javax.swing.SwingUtilities;

import org.json.simple.JSONObject;

import teaseManagers.EOSTeaseManager;


public class ImageAction extends EOSAction {
	private static final long serialVersionUID = 1L;
	String locator;
	
	public ImageAction(JSONObject parameters, EOSTeaseManager tm) {
		super(parameters, tm);
		locator=(String) parameters.get("locator");
	}
	
	public String getLocator() {
		return locator;
	}
	
	@Override
	public void run() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				teaseManager.setImage(getLocator());
			}
		});

		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
