package gui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class NotificationButton extends JButton {
	public NotificationButton(String buttonLabel, float size) {
		super(buttonLabel);
//		setContentAreaFilled(false);
		setOpaque(false);
		setBackground(new Color(0f, 0f, 0f, 0f));
		setFocusPainted(false);
		setBorderPainted(false);
//		setRolloverEnabled(false);
		setForeground(Color.white);
		setFont(getFont().deriveFont(size).deriveFont(Font.BOLD));
		setMargin(new Insets(6, 6, 6, 6));
		addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				Insets oldMargins=getMargin();
				if(getModel().isArmed()) {
					setMargin(new Insets(7, 6, 5, 6));
				}else {
					setMargin(new Insets(6, 6, 6, 6));
				}
				if(!oldMargins.equals(getMargin())) {
					updateUI();
				}
				
			}
		});
	}

	private static final long serialVersionUID = 1L;
	private static final Color lightColor=new Color(0.2f, 0.2f, 0.2f);
	private static final Color darkColor=new Color(0.05f, 0.05f, 0.05f);
	
	
	@Override
	public void paint(Graphics g) {
		
		GradientPaint gp;
		if (getModel().isRollover()) {
			gp=new GradientPaint(getWidth()/2, 0, lightColor.brighter(), getWidth()/2, getHeight(), darkColor.brighter());
		} else {
			gp=new GradientPaint(getWidth()/2, 0, lightColor, getWidth()/2, getHeight(), darkColor);
		}
		Graphics2D g2d=(Graphics2D) g.create();
		g2d.setPaint(gp);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (getModel().isArmed()) {
			g2d.fillRoundRect(0, 2, getWidth(), getHeight()-2, 10, 10);
			g2d.setColor(Color.black);
			g2d.drawRoundRect(0, 2, getWidth(), getHeight()-2, 10, 10);
		} else {
			g2d.fillRoundRect(0, 1, getWidth(), getHeight()-2, 10, 10);
			g2d.setColor(Color.black);
			g2d.drawRoundRect(0, 1, getWidth(), getHeight()-2, 10, 10);
		}
		
		super.paint(g);
	}
}
