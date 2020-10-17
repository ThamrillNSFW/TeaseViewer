package gui.eospanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;

import gui.components.NotificationPanel;

public class NotificationsPanel extends JPanel implements Scrollable{
	private static final long serialVersionUID = 1L;

	JPanel panel;
	Component filler;
	Insets defaultInsets;
	MouseListener ml;
	
	public static class InnerPanel extends JPanel implements Scrollable{
		private static final long serialVersionUID = 1L;

		public InnerPanel() {
			super();
			// TODO Auto-generated constructor stub
		}

		public InnerPanel(boolean isDoubleBuffered) {
			super(isDoubleBuffered);
			// TODO Auto-generated constructor stub
		}

		public InnerPanel(LayoutManager layout, boolean isDoubleBuffered) {
			super(layout, isDoubleBuffered);
			// TODO Auto-generated constructor stub
		}

		public InnerPanel(LayoutManager layout) {
			super(layout);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Dimension getPreferredScrollableViewportSize() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean getScrollableTracksViewportWidth() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean getScrollableTracksViewportHeight() {
			// TODO Auto-generated method stub
			return false;
		}
	}

	public NotificationsPanel() {
		super(new BorderLayout());
		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
//		setBorder(BorderFactory.createLineBorder(Color.red));
		filler = Box.createGlue();
		defaultInsets=new Insets(5,5,5,5);
		panel = new InnerPanel(new GridBagLayout());
		panel.add(filler, new GridBagConstraints(0, panel.getComponentCount(), 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, defaultInsets, 0, 0));
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
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				updateUI();
				repaint();
			}
		});
	}
	
//	@Override
//	public synchronized void addMouseListener(MouseListener l) {
//		super.addMouseListener(l);
//		panel.addMouseListener(l);
//		SwingUtilities.getAncestorOfClass(JScrollPane.class, panel).addMouseListener(l);
//		SwingUtilities.getAncestorOfClass(JViewport.class, panel).addMouseListener(l);
//		filler.addMouseListener(l);
//		ml=l;
//	}

	public void addNotification(NotificationPanel notification) {
		panel.remove(filler);
		panel.add(notification, new GridBagConstraints(0, panel.getComponentCount(), 1, 1, 1, .1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, defaultInsets, 0,0));
		panel.add(filler, new GridBagConstraints(0, panel.getComponentCount(), 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, defaultInsets, 0, 0));
		panel.updateUI();
		panel.repaint();
		updateUI();
		repaint();
	}

	public void clearNotifications() {
		for (int ii = 0; ii < panel.getComponentCount(); ii++) {
			if (panel.getComponent(ii) instanceof NotificationPanel) {
				NotificationPanel t = (NotificationPanel) panel.getComponent(ii);
				t.remove();
			}
		}
		panel.removeAll();
		panel.updateUI();
		panel.repaint();
	}

	public void removeNotification(NotificationPanel notificationPanel) {
		panel.remove(notificationPanel);
		panel.updateUI();
		panel.repaint();
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		// TODO Auto-generated method stub
		return false;
	}

	
}