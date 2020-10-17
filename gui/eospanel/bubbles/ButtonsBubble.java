package gui.eospanel.bubbles;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import teaseManagers.EOSTeaseManager;
import teaseManagers.eos.actions.ChoiceAction;
import teaseManagers.eos.actions.ChoiceAction.ChoiceActionOption;
import utilities.ColorUtilities;
import utilities.StringUtilities;

public class ButtonsBubble extends InputBubble {
	private static final long serialVersionUID = 1L;
	JPanel centerPanel;
	HashMap<JChoiceBubbleButton, Integer> mapping;
	EOSTeaseManager etm;
	
	public ButtonsBubble(ChoiceAction action, EOSTeaseManager tm) {
		setLayout(new BorderLayout());
		centerPanel=new JPanel(new GridBagLayout());
		Insets defaultInsets = new Insets(3, 3, 3, 3);
		action.setBubble(this);
		mapping=new HashMap<>();
		this.etm=tm;
		float fontSize=((Number) tm.getApp().getParameters().get("defaultFontSize")).floatValue(); 
		for(int ii=0; ii<action.getOptions().size(); ii++) {
			ChoiceActionOption option=action.getOptions().get(ii);
			Boolean visible;
			if(option.getVisible()==null) {
				visible=true;
			}else if(option.getVisible().startsWith("$")) {
				visible=tm.eval(option.getVisible().substring(1)).asBoolean();
			}else {
				visible=tm.eval(option.getVisible()).asBoolean();
			}
			if(visible) {
				JChoiceBubbleButton bb=new JChoiceBubbleButton(fontSize, option.getColor());
				bb.setText("<html>"+StringUtilities.convertStringForDisplay(option.getLabel(), ((String)tm.getApp().getParameters().get("emojiSet")), etm)+"</html>");
				mapping.put(bb, ii);
				bb.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						transform(bb.getText(), true, false, fontSize);
						tm.reportInput("choiceButton", mapping.get(e.getSource()));
					}
				});
				centerPanel.add(bb, new GridBagConstraints(centerPanel.getComponentCount(), 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
			}
		}
		add(centerPanel, BorderLayout.CENTER);
		centerPanel.setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}

	public static class JChoiceBubbleButton extends JButton {
		private static final long serialVersionUID = 1L;
		Color c;

		public JChoiceBubbleButton(float fontSize, String color) {
			setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			c=Color.decode(color);
			setContentAreaFilled(false);
			setFont(new JLabel().getFont().deriveFont(fontSize));
			setForeground(Color.WHITE);
			setFocusPainted(false);
			setBorderPainted(false);
			setRolloverEnabled(false);
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D graphics = (Graphics2D) g.create();

			int width = getWidth();
			int height = getHeight();

			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			Color color1;
			if (getModel().isArmed()) {
				color1=Color.darkGray;
			} else {
				color1=Color.darkGray.darker().darker();
			}
			
			Color color2=ColorUtilities.getUnsaturatedColor(color1);
			Paint paint=graphics.getPaint();
			GradientPaint gp = new GradientPaint(0, 0, color2, 0, height, color1);
			graphics.setPaint(gp);

			graphics.fillRoundRect(0, 0, width-1, height-1, 20, 20);
			graphics.setPaint(paint);
			graphics.setColor(Color.black);
//			graphics.setStroke(new BasicStroke(2f));
			graphics.drawRoundRect(0, 0, width-1, height-1, 20, 20);
			super.paintComponent(g);
			graphics.dispose();
			
		}
	}

	
}
