package gui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import gui.components.NotificationPanel;
import gui.components.Timer;
import gui.eospanel.BubblePanel;
import gui.eospanel.ImagePanel;
import gui.eospanel.NotificationsPanel;
import gui.eospanel.TimerPanel;
import teaseManagers.EOSTeaseManager;
import teaseManagers.eos.actions.ChoiceAction;
import teaseManagers.eos.actions.CreateNotificationAction;
import teaseManagers.eos.actions.PromptAction;
import teaseManagers.eos.actions.SayAction;
import teaseManagers.eos.actions.TimerAction;
import utilities.ColorUtilities;

public class EOSPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	ImagePanel imagePanel;
	BubblePanel bubblePanel;
	TimerPanel timerPanel;
	NotificationsPanel notificationPanel;
	static ImageIcon bg;
	EOSTeaseManager etm;

	public EOSPanel(EOSTeaseManager etm) {
		imagePanel = new ImagePanel();
		bubblePanel = new BubblePanel(etm.getApp());
		bubblePanel.setOpaque(false);
		timerPanel = new TimerPanel();
		notificationPanel = new NotificationsPanel();
		this.etm = etm;

		setBackground(Color.red);

		setLayout(null);
//		add(imagePanel);
		setComponentZOrder(imagePanel, 0);
		setComponentZOrder(timerPanel, 1);
		setComponentZOrder(notificationPanel, 0);

		Color clear = new Color(0, 0, 0, 0);
		JScrollPane jsp = new JScrollPane(bubblePanel);
		JScrollBar scrollBar = new JScrollBar(JScrollBar.VERTICAL) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return true;
			}
		};
		jsp.setVerticalScrollBar(scrollBar);
		jsp.setOpaque(false);
		jsp.setBackground(clear);
		jsp.getViewport().setBackground(clear);
		jsp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		bubblePanel.funct();
//		add(jsp);
		setComponentZOrder(jsp, 1);
//		addComponentListener(new ComponentAdapter() {
//			@Override
//			public void componentResized(ComponentEvent e) {
//				notificationPanel.updateUI();
//				notificationPanel.repaint();
//			}
//		});

		try {
			bg = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("resources/imgs/bg.png")));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		MouseListener ml = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				etm.reportInput("click", (Object[]) null);
			}
		};
		addMouseListener(ml);
		bubblePanel.setMouseListener(ml);
		imagePanel.addMouseListener(ml);
		jsp.addMouseListener(ml);
		jsp.getViewport().addMouseListener(ml);
		notificationPanel.addMouseListener(ml);
		timerPanel.addMouseListener(ml);
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				if (getWidth() * getHeight() > 0) {
					imagePanel.setBounds(0, 0, (int) getWidth(), (int) getHeight() * 3 / 5);
					jsp.setBounds(0, (int) getHeight() * 3 / 5, (int) getWidth(), (int) getHeight() * 2 / 5);
					timerPanel.setBounds((int) (getWidth() * 7 / 8), 0, (int) getWidth() / 8,
							(int) getHeight() * 3 / 5);
					notificationPanel.setBounds((int) (getWidth() * 5 / 6), (int) getHeight() * 3 / 5,
							(int) getWidth() / 6, (int) getHeight() * 2 / 5);
				}
			}
		});
	}

	Color cardinalColor = ColorUtilities.darkestGray;

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		super.paintComponent(g2d);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		int w = getWidth();
		int h = getHeight();

		Color color1 = Color.black;
		color1 = ColorUtilities.darkestGray;

		GradientPaint gp = new GradientPaint(0, 0, color1, w / 2, 0, cardinalColor);
		g2d.setPaint(gp);
		g2d.fillRect(0, 0, w / 2, h);
		gp = new GradientPaint(w / 2, 0, cardinalColor, w, 0, color1);
		g2d.setPaint(gp);
		g2d.fillRect(w / 2, 0, w, h);
		if (bg != null) {
			int tileWidth = bg.getImage().getWidth(null);
			int tileHeight = bg.getImage().getHeight(null);
			for (int y = 0; y < getHeight(); y += tileHeight) {
				for (int x = 0; x < getWidth(); x += tileWidth) {
					g2d.drawImage(bg.getImage(), x, y, this);
				}
			}
		}

		g2d.dispose();
	}

	public void setImage(Image image) {
		imagePanel.setImage(image);
		cardinalColor = ColorUtilities.getBackgroundCardinalColor(image,
				Math.min(image.getWidth(null), image.getHeight(null)) / 10);
	}

	public void createSayBubble(SayAction action) {
		bubblePanel.createSayBubble(action);
	}

	public void reset() {
		bubblePanel.removeAll();
		timerPanel.clearTimers();
	}

	public void createChoiceBubble(ChoiceAction action) {
		bubblePanel.createChoiceBubble(action);
	}

	public void createPromptBubble(PromptAction promptAction) {
		bubblePanel.createPromptBubble(promptAction);

	}

	public void createTimer(TimerAction timerAction) {
		Timer timer = new Timer(timerAction) {
			private static final long serialVersionUID = 1L;

			@Override
			public void completed() {
				super.completed();
				etm.reportInput("timer", (Object[]) null);
			}
		};
		switch (timerAction.getStyle().toLowerCase()) {
		case "hidden":
			timer.setVisible(false);
			break;
		case "secret":
			timer.setSecret(true);
			break;
		}
		timerPanel.addTimer(timer);
	}

	public NotificationPanel createNotificationAction(CreateNotificationAction createNotificationAction) {
		NotificationPanel notification = new NotificationPanel(createNotificationAction, etm.getApp());
		notificationPanel.addNotification(notification);
		return notification;
	}

	public BubblePanel getBubblePanel() {
		return bubblePanel;
	}

	public void clearTimers() {
		timerPanel.clearTimers();
	}

}
