package gui.eospanel.bubbles;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class InputBubble extends Bubble {
	private static final long serialVersionUID = 1L;
	
	public void transform(String msg, boolean leftEnabled, boolean rightEnabled, float fontSize) {
		removeAll();
		JLabel centerLabel=createLabel(msg, fontSize);
		centerLabel.setHorizontalAlignment(SwingConstants.CENTER);
		Component left=createLabel(">", fontSize);
		Component right=createLabel("<", fontSize);
		
		if(!leftEnabled) {
			left=Box.createHorizontalStrut((int) left.getPreferredSize().getWidth());
		}
		
		if(!rightEnabled) {
			right=Box.createHorizontalStrut((int) right.getPreferredSize().getWidth());
		}
		setLayout(new BorderLayout());
		add(centerLabel, BorderLayout.CENTER);
		add(left, BorderLayout.WEST);
		add(right, BorderLayout.EAST);
		updateUI();
		repaint();
		((JComponent)getParent()).updateUI();
		((JComponent)getParent()).repaint();
	}
	
	public JLabel createLabel(String txt, float fontSize) {
		JLabel label=new JLabel(txt);
		label.setFont(label.getFont().deriveFont(fontSize));
		label.setForeground(Color.WHITE);
		label.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		label.setOpaque(false);
		label.setBackground(new Color(0, 0, 0, 0));
		return label;
	}
}
