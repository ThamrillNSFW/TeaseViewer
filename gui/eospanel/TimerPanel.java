package gui.eospanel;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import gui.components.Timer;

public class TimerPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	JPanel panel;
	
	public TimerPanel() {
		super(new BorderLayout());
		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		panel=new JPanel(new GridLayout(0, 1));
		panel.setOpaque(false);
		
		JScrollPane jsp = new JScrollPane(panel);
		JScrollBar scrollBar = new JScrollBar(JScrollBar.VERTICAL) {
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isVisible() {
                return true;
            }
        };
        jsp.setVerticalScrollBar(scrollBar);
		jsp.setOpaque(false);
		jsp.getViewport().setOpaque(false);
		jsp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		add(jsp, BorderLayout.CENTER);
	}
	
	public void addTimer(Timer timer) {
		panel.add(timer);
		panel.updateUI();
		panel.repaint();
	}
	
	public void clearTimers() {
		for(int ii=0; ii<panel.getComponentCount();ii++) {
			if(panel.getComponent(ii) instanceof Timer) {
				Timer t=(Timer) panel.getComponent(ii);
				t.cancel();
			}
		}
		panel.removeAll();
		panel.updateUI();
		panel.repaint();
	}
}
