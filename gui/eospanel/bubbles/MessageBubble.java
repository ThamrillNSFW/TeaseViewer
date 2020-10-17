package gui.eospanel.bubbles;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import gui.eospanel.BubblePanel;

public class MessageBubble extends Bubble {
	private static final long serialVersionUID = 1L;
	JPanel tempPanel;
	Thread t;

	public MessageBubble(String text, int alignament, float size) {

		JLabel label;

		switch (alignament) {
		case BubblePanel.LEFT:
			label = new JLabel("<html><body style=\"text-align:left;\">" + text + "</body></html>", SwingConstants.LEFT);
			break;
		case BubblePanel.CENTER:
			label = new JLabel("<html><body style=\"text-align:center;\">" + text + "</body></html>", SwingConstants.CENTER);
			break;
		case BubblePanel.RIGHT:
			label = new JLabel("<html><body style=\"text-align:right;\">" + text + "</body></html>", SwingConstants.RIGHT);
			break;
		default:
			label = new JLabel("<html>" + text + "</html>", SwingConstants.CENTER);
		}
		label.setForeground(Color.white);
		label.setFont(label.getFont().deriveFont(size));

		add(label, BorderLayout.CENTER);
		tempPanel=new JPanel(new BorderLayout());
		tempPanel.setOpaque(false);
		try {
			ImageIcon icon=new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("resources/icons/Next.png")));
			JLabel label1=new JLabel(icon);
			label1.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			label1.setOpaque(false);
			
			
			tempPanel.add(label1, BorderLayout.PAGE_END);
		} catch (IOException e) {
			e.printStackTrace();
		}
		add(tempPanel, BorderLayout.LINE_END);
		add(Box.createRigidArea(new Dimension(17,17)), BorderLayout.LINE_START);
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}
	
	public void unfocus() {
		if(tempPanel!=null) {
			remove(tempPanel);
			add(Box.createRigidArea(new Dimension(17,17)), BorderLayout.LINE_END);
			repaint();
			tempPanel=null;
		}
	}

}
