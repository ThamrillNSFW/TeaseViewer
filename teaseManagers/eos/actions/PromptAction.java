package teaseManagers.eos.actions;

import javax.swing.SwingUtilities;

import org.json.simple.JSONObject;

import gui.eospanel.bubbles.PromptBubble;
import teaseManagers.EOSTeaseManager;

public class PromptAction extends EOSAction {
	private static final long serialVersionUID = 1L;
	
	String variable;

	private PromptBubble bubble;
	
	public PromptAction(JSONObject parameters, EOSTeaseManager tm) {
		super(parameters, tm);
		variable=(String) parameters.get("variable");
	}
	
	@Override
	public void run() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				teaseManager.createPromptBubble(PromptAction.this);
			}
		});

		while (true) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (pageExecutor.getInputs().containsKey("notification")) {
				if(bubble!=null) {
					bubble.transform("...", true, true, ((Number) teaseManager.getApp().getParameters().get("defaultFontSize")).floatValue() );
				}
				break;
			}
			if (pageExecutor.getInputs().containsKey("timer")) {
				if(bubble!=null) {
					bubble.transform("...", true, true, ((Number) teaseManager.getApp().getParameters().get("defaultFontSize")).floatValue() );
				}
				break;
			}
			if (pageExecutor.getInputs().containsKey("prompt")&&(pageExecutor.getInputs().get("prompt")[0] instanceof String)) {
				teaseManager.eval(variable+"='"+pageExecutor.getInputs().get("prompt")[0]+"'");
				break;
			}
		}
	}

	public void setBubble(PromptBubble bubble) {
		this.bubble = bubble;
	}
	
}
