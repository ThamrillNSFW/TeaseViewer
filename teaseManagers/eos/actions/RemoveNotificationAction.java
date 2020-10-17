package teaseManagers.eos.actions;

import javax.swing.SwingUtilities;

import org.json.simple.JSONObject;

import teaseManagers.EOSTeaseManager;

public class RemoveNotificationAction extends EOSAction {
	private static final long serialVersionUID = 1L;
	
	String id;

	public RemoveNotificationAction(JSONObject parameters, EOSTeaseManager tm) {
		super(parameters, tm);
		for(Object o:parameters.keySet()) {
			switch((String)o) {
			case "id":
				id=(String) parameters.get((String)o);
				break;
			}
		}
	}
	
	@Override
	public void run() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				teaseManager.getNotificationManager().removeNotificationAction(RemoveNotificationAction.this);
			}
		});

//		try {
//			Thread.sleep(100);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}

	public String getId() {
		return id;
	}
}
