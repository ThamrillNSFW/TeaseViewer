package teaseManagers.eos.actions;

import org.json.simple.JSONObject;

import teaseManagers.EOSTeaseManager;

public class PlayAudioAction extends EOSAction {
	private static final long serialVersionUID = 1L;
	String locator;
	int loops;
	String id;
	boolean background;
	Number volume;

	public PlayAudioAction(JSONObject parameters, EOSTeaseManager tm) {
		super(parameters, tm);
		loops=1;
		for(Object o:parameters.keySet()) {
			switch((String)o) {
			case "locator":
				locator=(String) parameters.get((String)o);
				break;
			case "loops":
				loops=((Long) parameters.get((String)o)).intValue();
				break;
			case "id":
				id=(String) parameters.get((String)o);
				break;
			case "background":
				background=(boolean) parameters.get((String)o);
				break;
			case "volume":
				volume=((Number) parameters.get((String)o));
				break;
			}
		}
	}
	
	public String getLocator() {
		return locator;
	}
	
	public int getLoops() {
		return loops;
	}
	
	public String getId() {
		return id;
	}
	
	public boolean isBackground() {
		return background;
	}
	
	public Number getVolume() {
		return volume;
	}
	
	@Override
	public void run() {
		teaseManager.getAudioManager().playAudioAction(PlayAudioAction.this, teaseManager.getFs());

		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
