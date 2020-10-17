package teaseManagers;

import java.util.TreeMap;

import gui.components.NotificationPanel;
import system.TeaseViewer;
import teaseManagers.eos.actions.CreateNotificationAction;
import teaseManagers.eos.actions.RemoveNotificationAction;

public class NotificationManager {
	EOSTeaseManager manager;

	TreeMap<String, NotificationPanel> notifications;

	public NotificationManager(TeaseViewer app, EOSTeaseManager eosTeaseManager) {
		manager = eosTeaseManager;
		notifications = new TreeMap<>();
	}

	public void createNotificationAction(CreateNotificationAction createNotificationAction) {
		NotificationPanel notification = manager.createNotificationAction(createNotificationAction);
		notifications.put(createNotificationAction.getId(), notification);
	}

	public void removeNotificationAction(RemoveNotificationAction removeNotificationAction) {
		NotificationPanel notification = notifications.remove(removeNotificationAction.getId());
		if (notification != null) {
			notification.remove();
		}
	}

}