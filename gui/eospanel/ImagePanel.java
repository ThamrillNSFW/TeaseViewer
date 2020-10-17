package gui.eospanel;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

public class ImagePanel extends JLayeredPane {
	private static final long serialVersionUID = 1L;
//	TimerPanel tp;
//	NotificationsPanel np;
	Image image;
	JLabel imageLabel;
	
	public ImagePanel() {
		setOpaque(false);
		setLayout(null);
		imageLabel=new JLabel();
		imageLabel.setOpaque(false);
		imageLabel.setBackground(new Color(0f, 0f, 0f, 0f));
		setComponentZOrder(imageLabel, 0);
//		np=new NotificationsPanel();
//		tp=new TimerPanel();
//		setComponentZOrder(np, 1);
//		setComponentZOrder(tp, 2);
		
		addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentResized(ComponentEvent e) {
				updateImage();
			}
		});
	}
	
	public void setImage(Image image) {
		this.image=image;
		updateImage();
	}
	
	public void updateImage() {
		if(image==null) {
			return;
		}
		int width=image.getWidth(null);
		int height=image.getHeight(null);
		int availableWidth=getWidth();
		int availableHeigth=getHeight();
		float scale=Math.max(height*1f/availableHeigth, width*1f/availableWidth);
		scale=scale>1?scale:1;
		width=(int) (width/scale);
		height=(int) (height/scale);
		if(width*height==0) {
			return;
		}
		imageLabel.setIcon(new ImageIcon(image.getScaledInstance(width, height, Image.SCALE_FAST)));
		imageLabel.setBounds(availableWidth/2-width/2, availableHeigth/2-height/2, width, height);
		imageLabel.updateUI();
		imageLabel.repaint();
		updateUI();
		repaint();
		getParent().repaint();
	}
}
