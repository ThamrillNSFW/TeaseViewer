package gui.eospanel.bubbles;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JPanel;

public class Bubble extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public Bubble() {
		super(new BorderLayout(), false);
		background = new Color(0.1f, 0.1f, 0.1f, 0.8f);
		setOpaque(false);
		addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent e) {
			}

			@Override
			public void componentResized(ComponentEvent e) {
				Rectangle rect = getBounds();
				rect.grow(2, 2);
				scrollRectToVisible(rect);
			}

			@Override
			public void componentMoved(ComponentEvent e) {
			}

			@Override
			public void componentHidden(ComponentEvent e) {
			}
		});
	}
	
	protected Dimension arcs = new Dimension(20, 20);
	Color background;
	
	@Override
	protected void paintComponent(Graphics g) {
		
		Graphics2D graphics = (Graphics2D) g.create();
		super.paintComponent(graphics);
		int width = getWidth();
		int height = getHeight();
		
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setColor(background);
		graphics.fillRoundRect(0, 0, width-1 , height-1 , arcs.width, arcs.height);
		graphics.setColor(getForeground());
		graphics.drawRoundRect(0, 0, width-1, height-1, arcs.width, arcs.height);
		graphics.dispose();
	}
}
