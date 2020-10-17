package gui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.graalvm.polyglot.HostAccess;

import gui.eospanel.NotificationsPanel;
import system.TeaseViewer;
import teaseManagers.EOSTeaseManager;
import teaseManagers.eos.actions.CreateNotificationAction;
import teaseManagers.eos.actions.EOSAction;
import utilities.StringUtilities;

public class NotificationPanel extends JPanel implements ActionListener, TimerListener {
	private static final long serialVersionUID = 1L;
	public static final int angularResolution = 20;
	boolean flag;
	JLabel titleLabel;
	NotificationButton notificationButton;
	NotificationTimer notificationTimer;
	ArrayList<EOSAction> buttonActions;
	ArrayList<EOSAction> timerActions;
	EOSTeaseManager etm;

	public NotificationPanel(CreateNotificationAction createNotificationAction, TeaseViewer app) {
		this(createNotificationAction.getLabel(), createNotificationAction.getButtonLabel(),
				createNotificationAction.getButtonCommands(), createNotificationAction.getDuration(),
				createNotificationAction.getTimerCommands(), app);
	}

	public NotificationPanel(String label, String buttonLabel, ArrayList<EOSAction> buttonActions, Long duration,
			ArrayList<EOSAction> timerActions, TeaseViewer app) {
		super(new GridBagLayout());
		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		etm = (EOSTeaseManager) app.getTeaseManager();
		flag = false;
		float fontSize = ((Number) app.getParameters().get("defaultFontSize")).floatValue();
		Insets defaultInsets = new Insets(1, 1, 1, 1);
		if (label != null && !label.isBlank()) {
			String label_1 = StringUtilities.convertStringForDisplay(label,
					(String) app.getParameters().get("emojiSet"), etm);
			titleLabel = new JLabel("<html><body style=\"text-align:center;\">" + label_1 + "</body></html>",
					SwingConstants.CENTER);
			titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
			titleLabel.setForeground(Color.WHITE);
			titleLabel.setFont(titleLabel.getFont().deriveFont(fontSize).deriveFont(Font.BOLD));
			add(titleLabel, new GridBagConstraints(0, getComponentCount(), 1, 1, 1, 1, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, defaultInsets, 0, 0));
		}
		if (buttonLabel != null) {
			String buttonLabel_1 = StringUtilities.convertStringForDisplay(buttonLabel,
					(String) app.getParameters().get("emojiSet"), etm);
			notificationButton = new NotificationButton("<html><body style=\"text-align:center;\">" + buttonLabel_1 + "</body></html>", fontSize);
			notificationButton.addActionListener(this);
//			notificationButton.setMinimumSize(notificationButton.getPreferredSize());
			notificationButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			add(notificationButton, new GridBagConstraints(0, getComponentCount(), 1, 1, 1, 1,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH, defaultInsets, 0, 0));
			this.buttonActions = buttonActions;
		}
		if (duration != null) {
			notificationTimer = new NotificationTimer(duration);
			add(notificationTimer, new GridBagConstraints(0, getComponentCount(), 1, 1, 1, 0, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, new Insets(4, 1, 4, 1), 0, 0));
			this.timerActions = timerActions;
		}
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(Color.black);
		g2d.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
		g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
		super.paint(g);
	}

	@HostAccess.Export
	public void remove() {
		if (notificationTimer != null) {
			notificationTimer.cancel();
		}
		NotificationsPanel np = (NotificationsPanel) SwingUtilities.getAncestorOfClass(NotificationsPanel.class, this);
		if (np != null) {
			np.removeNotification(this);
		}
	}

	@HostAccess.Export
	public void setTitle(String str) {
		String label_1 = StringUtilities.convertStringForDisplay(str,
				(String) etm.getApp().getParameters().get("emojiSet"), etm);
		titleLabel.setText("<html><body style=\"text-align:center;\">" + label_1 + "</body></html>");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		etm.getEosPageExecutor().injectCommands(buttonActions);
		etm.reportInput("notification", (Object) null);
		remove();
	}

	public void removeFromNotificationsPanel() {
		NotificationsPanel np = (NotificationsPanel) SwingUtilities.getAncestorOfClass(NotificationsPanel.class, this);
		np.removeNotification(this);
	}

	@Override
	public void timerFinished() {
		etm.getEosPageExecutor().injectCommands(timerActions);
		etm.reportInput("notification", (Object) null);
		remove();
	}

	@Override
	public void timerStarted() {
	}
}
