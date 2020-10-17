package gui.eospanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.Scrollable;

import gui.eospanel.bubbles.Bubble;
import gui.eospanel.bubbles.ButtonsBubble;
import gui.eospanel.bubbles.MessageBubble;
import gui.eospanel.bubbles.PromptBubble;
import system.TeaseViewer;
import teaseManagers.EOSTeaseManager;
import teaseManagers.eos.actions.ChoiceAction;
import teaseManagers.eos.actions.PromptAction;
import teaseManagers.eos.actions.SayAction;
import utilities.StringUtilities;

public class BubblePanel extends JPanel implements Scrollable {
	private static final long serialVersionUID = 1L;
	public static final int LEFT = -1;
	public static final int RIGHT = +1;
	public static final int CENTER = 0;
	Insets insets;
	TeaseViewer app;
	MouseListener ml;

	HashMap<Bubble, Object[]> lines;

	public BubblePanel(TeaseViewer app) {
		super(new GridBagLayout());
		this.app = app;
		insets = new Insets(5, 5, 5, 5);
		setDoubleBuffered(true);
		lines = new HashMap<>();
	}

	public void setMouseListener(MouseListener ml) {
		addMouseListener(ml);
		this.ml = ml;
	}

	public void funct() {
		getParent().getParent().addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				float weight;
				Object[] tempObjects;
				int align;

				for (Bubble bubble : lines.keySet()) {
					weight = (float) (bubble.getPreferredSize().getWidth() * 1.0f / getWidth());
					if (weight > 0.8) {
						weight = 0.8f;
					}

					tempObjects = lines.get(bubble);
					align = (int) tempObjects[0];
					JPanel wrapperPanel = (JPanel) tempObjects[1];
					GridBagLayout layout = (GridBagLayout) wrapperPanel.getLayout();
					layout.getConstraints(bubble);

					GridBagConstraints bubbleConstraints;
					GridBagConstraints fill1Constraints;
					GridBagConstraints fill2Constraints;
					switch (align) {
					case -1:
						bubbleConstraints = new GridBagConstraints(0, 0, 3, 1, weight, 0.1,
								GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, insets, 0, 0);
						fill1Constraints = new GridBagConstraints(3, 0, 1, 1, 10*(1 - weight), 0,
								GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0);
						fill2Constraints = new GridBagConstraints(4, 0, 1, 1, 10*(1 - weight), 0,
								GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0);
						break;
					case 0:
						bubbleConstraints = new GridBagConstraints(1, 0, 3, 1, weight, 0.1, GridBagConstraints.CENTER,
								GridBagConstraints.HORIZONTAL, insets, 0, 0);
						fill1Constraints = new GridBagConstraints(0, 0, 1, 1, 10*(1 - weight), 0.1,
								GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0);
						fill2Constraints = new GridBagConstraints(4, 0, 1, 1, 10*(1 - weight), 0.1,
								GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0);
						break;
					case 1:
						bubbleConstraints = new GridBagConstraints(2, 0, 3, 1, weight, 0.1, GridBagConstraints.LINE_END,
								GridBagConstraints.HORIZONTAL, insets, 0, 0);
						fill1Constraints = new GridBagConstraints(0, 0, 1, 1, 10*(1 - weight), 0,
								GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0);
						fill2Constraints = new GridBagConstraints(1, 0, 1, 1, 10*(1 - weight), 0,
								GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0);
						break;
					default:
						throw new IllegalArgumentException("Unexpected value: " + align);
					}
					wrapperPanel.removeAll();
					wrapperPanel.add(bubble, bubbleConstraints);
					wrapperPanel.add(Box.createGlue(), fill1Constraints);
					wrapperPanel.add(Box.createGlue(), fill2Constraints);
				}
			}
		});
	}

	public void addBubble(Bubble bubble, int alignament) {
		int y = (getComponentCount()) + 1;
		GridBagConstraints bubbleConstraints;
		GridBagConstraints fill1Constraints;
		GridBagConstraints fill2Constraints;
		JPanel wrapperPanel = new JPanel(new GridBagLayout());
		wrapperPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				wrapperPanel.scrollRectToVisible(wrapperPanel.getBounds());
			}
		});
		wrapperPanel.setOpaque(false);
		wrapperPanel.setBackground(new Color(0, 0, 0, 0));
		
		float weight = (float) (bubble.getPreferredSize().getWidth() * 1.0f / getWidth());
		if (weight > 0.8) {
			weight = 0.8f;
		}
		switch (alignament) {
		case -1:
			bubbleConstraints = new GridBagConstraints(0, 0, 3, 1, weight, 0.1, GridBagConstraints.LINE_START,
					GridBagConstraints.HORIZONTAL, insets, 0, 0);
			fill1Constraints = new GridBagConstraints(3, 0, 1, 1, (1 - weight) * 2, 0, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, insets, 0, 0);
			fill2Constraints = new GridBagConstraints(4, 0, 1, 1, (1 - weight) * 2, 0, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, insets, 0, 0);
			break;
		case 0:
			bubbleConstraints = new GridBagConstraints(1, 0, 3, 1, weight, 0.1, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, insets, 0, 0);
			fill1Constraints = new GridBagConstraints(0, 0, 1, 1, (1 - weight), 0.1, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, insets, 0, 0);
			fill2Constraints = new GridBagConstraints(4, 0, 1, 1, (1 - weight), 0.1, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, insets, 0, 0);
			break;
		case 1:
			bubbleConstraints = new GridBagConstraints(2, 0, 3, 1, weight, 0.1, GridBagConstraints.LINE_END,
					GridBagConstraints.HORIZONTAL, insets, 0, 0);
			fill1Constraints = new GridBagConstraints(0, 0, 1, 1, (1 - weight), 0, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, insets, 0, 0);
			fill2Constraints = new GridBagConstraints(1, 0, 1, 1, (1 - weight), 0, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, insets, 0, 0);
			break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + alignament);
		}
		JComponent glue1 = (JComponent) Box.createGlue();
		JComponent glue2 = (JComponent) Box.createGlue();
		unfocus();
		lines.put(bubble, new Object[] { alignament, wrapperPanel });
		wrapperPanel.add(bubble, bubbleConstraints);
		wrapperPanel.add(glue1, fill1Constraints);
		wrapperPanel.add(glue2, fill2Constraints);
		wrapperPanel.addMouseListener(ml);
		bubble.addMouseListener(ml);

		add(wrapperPanel, new GridBagConstraints(0, y, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				insets, 0, 0));
		updateUI();
		repaint();
		((JComponent) getParent()).updateUI();
		((JComponent) getParent()).repaint();
	}

	public void unfocus() {
		for (Bubble b : lines.keySet()) {
			if (b instanceof MessageBubble) {
				((MessageBubble) b).unfocus();
			}
		}
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 100;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 50;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	public void createSayBubble(SayAction action) {
		int align = CENTER;
		switch (action.getAlign().toLowerCase()) {
		case "left":
			align = LEFT;
			break;
		case "center":
			align = CENTER;
			break;
		case "right":
			align = RIGHT;
			break;
		}
		String msg = action.getLabel();
		msg = StringUtilities.convertStringForDisplay(msg, (String) app.getParameters().get("emojiSet"), (EOSTeaseManager) app.getTeaseManager());
		MessageBubble messageBubble = new MessageBubble(msg, align,
				((Number) app.getParameters().get("defaultFontSize")).floatValue());
		addBubble(messageBubble, align);
	}

	public void createChoiceBubble(ChoiceAction action) {
		int align = CENTER;

		ButtonsBubble buttonsBubble = new ButtonsBubble(action, (EOSTeaseManager) app.getTeaseManager());
		addBubble(buttonsBubble, align);
	}

	public void createPromptBubble(PromptAction promptAction) {
		int align = CENTER;

		PromptBubble promptBubble = new PromptBubble(promptAction, (EOSTeaseManager) app.getTeaseManager());
		promptAction.setBubble(promptBubble);
		addBubble(promptBubble, align);
		requestFocus();
	}

}
