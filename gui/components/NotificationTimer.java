package gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;

public class NotificationTimer extends JPanel {
	private static final long serialVersionUID = 1L;

	ArrayList<TimerListener> timerListeners;
	boolean flag;

	float progress=0f;
	public NotificationTimer(Long duration) {
		timerListeners = new ArrayList<>();
		setMinimumSize(new Dimension(3, 3));
		setPreferredSize(getMinimumSize());
		Thread t=new Thread(new Runnable() {
			
			@Override
			public void run() {
				for(TimerListener timerListener:timerListeners) {
					timerListener.timerStarted();
				}
				long start=System.currentTimeMillis();
				long elapsed=0;
				int toPass=50;
				while(elapsed<duration*1000) {
					toPass=(int) (duration*1000-elapsed<toPass?duration*1000-elapsed:toPass);
					if(flag) {
						return;
					}
					try {
						Thread.sleep(toPass);
					} catch (InterruptedException e) {
						return;
					}
					
					elapsed=System.currentTimeMillis()-start;
					progress=elapsed*1.0f/(duration*1000);
					updateUI();
				}
				for(TimerListener timerListener:timerListeners) {
					timerListener.timerFinished();
				}
			}
		});
		t.start();
	}

	public void addTimerListener(TimerListener listener) {
		timerListeners.add(listener);
	}

	public void removeTimerListener(TimerListener listener) {
		timerListeners.remove(listener);
	}

	public float getProgress() {
		return progress;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, (int) (getWidth()), getHeight());
		g2d.setColor(Color.red);
		g2d.fillRect(0, 0, (int) (getWidth() * getProgress()), getHeight());
	}

	public void cancel() {
		flag=true;
	}
}
