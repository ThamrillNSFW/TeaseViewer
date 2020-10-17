package teaseManagers.eos.actions;

import java.util.TreeMap;

import javax.swing.SwingUtilities;

import org.json.simple.JSONObject;

import teaseManagers.EOSTeaseManager;
import teaseManagers.eos.ActionFactory;


public class SayAction extends EOSAction{
	private static final long serialVersionUID = 1L;
	String label;
	String mode;
	String align;
	String duration;
	boolean allowSkip;
	
	public SayAction(JSONObject parameters, EOSTeaseManager tm) {
		super(parameters, tm);
		align="center";
		mode="auto";
		for(Object o:parameters.keySet()) {
			if(parameters.get((String)o)==null) {
				continue;
			}
			switch((String)o) {
			case "label":
				label=(String) parameters.get((String)o);
				break;
			case "mode":
				mode=(String) parameters.get((String)o);
				break;
			case "align":
				align=(String) parameters.get((String)o);
				break;
			case "duration":
				duration=(String) parameters.get((String)o);
				break;
			case "allowSkip":
				allowSkip=(Boolean) parameters.get((String)o);
				break;
			}
		}
	}
	
	public String getMode() {
		return mode;
	}
	
	public String getDuration() {
		return duration;
	}
	
	public boolean isAllowSkip() {
		return allowSkip;
	}
	
	public String getAlign() {
		return align;
	}
	
	public String getLabel() {
		return label;
	}
	
	@Override
	public void run() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				teaseManager.createSayBubble(SayAction.this);
			}
		});
		long duration=this.duration==null?estimateWait(label):ActionFactory.duration2Millis(this.duration.toLowerCase());
		if(mode.equalsIgnoreCase("auto")) {
			EOSAction nextAction=pageExecutor.getActions().peek();
			if(nextAction instanceof TimerAction) {
				mode="instant";
			}else 
				if(nextAction instanceof ChoiceAction) {
				mode="instant";
			}else if(nextAction instanceof PromptAction) {
				mode="instant";
//			}else if(nextAction instanceof ImageAction) {
//				mode="instant";
			}else {
				mode="pause";
			}
		}
		if(mode.equalsIgnoreCase("instant")) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return;
		}
		while (true) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			duration-=50;
			if (pageExecutor.getInputs().containsKey("notification")) {
				break;
			}
			if (pageExecutor.getInputs().containsKey("click") && (mode.equalsIgnoreCase("pause")||(isAllowSkip()))) {
				break;
			}
			if((mode.equalsIgnoreCase("custom")||mode.equalsIgnoreCase("autoplay"))&&duration<=0) {
				break;
			}
		}
	}
	
	public int estimateWait(String text) {
		return text.length()*30+1800;
	}

}
