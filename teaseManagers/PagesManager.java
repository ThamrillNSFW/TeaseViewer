package teaseManagers;

import java.util.LinkedList;
import java.util.TreeMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import system.Logger;
import system.TeaseViewer;

public class PagesManager {

	TreeMap<String, JSONArray> pages;
	TreeMap<String, Boolean> enabled;

	String currentPageID;
	
	LinkedList<JSONObject> actionQueue;
	
	TeaseViewer app;
	
	public PagesManager(TeaseViewer app) {
		this.app=app;
	}

	public void setPages(JSONObject json) {
		this.pages = new TreeMap<>();
		for (Object key : json.keySet()) {
			pages.put((String) key, (JSONArray) json.get(key));
		}
	}

	public void goTo(String pageID) {
		if (!pages.containsKey(pageID)) {
			Logger.staticLog("Tried serching for nonexistant page: "+pageID, Logger.ERROR);
			return;
		}
		currentPageID=pageID;
		actionQueue=new LinkedList<>();
		JSONArray newActionQueue=pages.get(pageID);
		for(Object o:newActionQueue) {
			actionQueue.add((JSONObject) o);
		}
		pageChanged();
	}
	
	public LinkedList<JSONObject> getActionQueue() {
		return actionQueue;
	}
	
	public void setActionQueue(LinkedList<JSONObject> actionQueue) {
		this.actionQueue = actionQueue;
	}

	public String getCurrentPageID() {
		return currentPageID;
	}

	public void enable(String pageID) {
		if(!pages.containsKey(pageID)) {
			Logger.staticLog("Tried serching for nonexistant page: "+pageID, Logger.ERROR);
		}
		enabled.put(pageID, true);
	}

	public void disable(String pageID) {
		if(!pages.containsKey(pageID)) {
			Logger.staticLog("Tried serching for nonexistant page: "+pageID, Logger.ERROR);
		}
		enabled.put(pageID, false);
	}
	
	public boolean isEnabled(String pageID) {
		if(!pages.containsKey(pageID)) {
			Logger.staticLog("Tried serching for nonexistant page: "+pageID, Logger.ERROR);
			return false;
		}
		return enabled.get(pageID);
	}
	
	public void pageChanged() {
		
	};
}