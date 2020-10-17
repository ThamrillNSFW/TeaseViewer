package teaseManagers.eos;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;

import system.Logger;
import teaseManagers.EOSTeaseManager;
import teaseManagers.eos.actions.ChoiceAction;
import teaseManagers.eos.actions.CreateNotificationAction;
import teaseManagers.eos.actions.EOSAction;
import teaseManagers.eos.actions.EndAction;
import teaseManagers.eos.actions.EvalAction;
import teaseManagers.eos.actions.GoToAction;
import teaseManagers.eos.actions.IfAction;
import teaseManagers.eos.actions.ImageAction;
import teaseManagers.eos.actions.PlayAudioAction;
import teaseManagers.eos.actions.PromptAction;
import teaseManagers.eos.actions.RemoveNotificationAction;
import teaseManagers.eos.actions.SayAction;
import teaseManagers.eos.actions.TimerAction;

public class ActionFactory {
	
	public static EOSAction createAction(JSONObject object, EOSTeaseManager tm) {
		@SuppressWarnings("unchecked")
		ArrayList<Object> keys=new ArrayList<Object>(object.keySet());
		String actionType=(String) keys.get(0);
		switch(actionType) {
		case "image":
			return new ImageAction((JSONObject) object.get("image"), tm);
		case "say":
			return new SayAction((JSONObject) object.get("say"), tm);
		case "goto":
			return new GoToAction((JSONObject) object.get("goto"), tm);
		case "choice":
			return new ChoiceAction((JSONObject) object.get("choice"), tm);
		case "end":
			return new EndAction((JSONObject) object.get("end"), tm);
		case "prompt":
			return new PromptAction((JSONObject) object.get("prompt"), tm);
		case "eval":
			return new EvalAction((JSONObject) object.get("eval"), tm);
		case "audio.play":
			return new PlayAudioAction((JSONObject) object.get("audio.play"), tm);
		case "notification.create":
			return new CreateNotificationAction((JSONObject) object.get("notification.create"), tm);
		case "notification.remove":
			return new RemoveNotificationAction((JSONObject) object.get("notification.remove"), tm);
		case "timer":
			return new TimerAction((JSONObject) object.get("timer"), tm);
		case "if":
			return new IfAction((JSONObject) object.get("if"), tm);
		case "noop":
			return null;
			default:
				Logger.staticLog("Missing action type:"+actionType, Logger.ERROR);
		}
		return null;
	}
	
	public static long duration2Millis(String duration) {
		if(duration==null) {
			return 0;
		}
		long time=0;
		Pattern pattern=Pattern.compile("(\\d+)w");
		Matcher matcher=pattern.matcher(duration);
		if(matcher.find()) {
			time=Long.parseLong(matcher.group(1));
		}
		time*=7;
		pattern=Pattern.compile("(\\d+)d");
		matcher=pattern.matcher(duration);
		if(matcher.find()) {
			time+=Long.parseLong(matcher.group(1));
		}
		time*=24;
		pattern=Pattern.compile("(\\d+)h");
		matcher=pattern.matcher(duration);
		if(matcher.find()) {
			time+=Long.parseLong(matcher.group(1));
		}
		time*=60;
		pattern=Pattern.compile("(\\d+)m");
		matcher=pattern.matcher(duration);
		if(matcher.find()) {
			time+=Long.parseLong(matcher.group(1));
		}
		time*=60;
		pattern=Pattern.compile("(\\d+)s");
		matcher=pattern.matcher(duration);
		if(matcher.find()) {
			time+=Long.parseLong(matcher.group(1));
		}
		time*=1000;
		return time;
	}

}
