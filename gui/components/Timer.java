package gui.components;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import teaseManagers.eos.actions.TimerAction;

public class Timer extends JPanel {
	private static final long serialVersionUID = 1L;
	long seconds = 0;
	public static final int angularResolution = 20;
	int progress;
	boolean flag;
	TimerAction action;

	public Timer(TimerAction timerAction) {
		this(timerAction.getSeconds());
		action=timerAction;
	}

	public Timer(long duration) {
		this.seconds = duration;
		setOpaque(false);
		flag = false;
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				Timer timer = Timer.this;
				long milliseconds = timer.seconds * 1000 / (360 * angularResolution + 1);
				if(milliseconds<=0) {
					milliseconds=1;
				}
				long reps;
				if (milliseconds > 0) {
					reps = timer.seconds * 1000 / milliseconds;
					for (int ii = 0; ii < reps; ii++) {
						try {
							Thread.sleep(milliseconds);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						timer.setProgress(ii * 1.0f * milliseconds / (timer.seconds * 1000));
						centiSeconds = (long) (ii * 1.0f * milliseconds / 10);
						timer.repaint();
						if (flag) {
							return;
						}
					}
				}

				completed();
				JComponent parent = (JComponent) getParent();
				getParent().remove(Timer.this);
				parent.updateUI();
				parent.repaint();
			}
		});
		t.start();
	}

	public int getProgress() {
		return progress;
	}

	public void completed() {
		flag = true;
		if(action!=null&&action.isAsync()) {
			action.getManager().getEosPageExecutor().injectCommands(action.getCommands());
		}
	}

	public void setProgress(float progress) {
		this.progress = Math.round(progress * 360);
	}

	public String getResidualTime() {
		int seconds = (int) Math.ceil(this.seconds - getProgress() / 360f * this.seconds);
		if (seconds > 60) {
			int minutes = seconds / 60;
			seconds = seconds - minutes * 60;
			return Integer.toString(minutes) + ":" + String.format("%02d", seconds);
		}
		return Integer.toString(seconds);
	}

	long centiSeconds;

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		int size = Math.min(getWidth(), getHeight());
		if (size % 2 == 0) {
			size--;
		}
		int thickness = size / 10;
		int border = size / 15;
		border -= border % 2 == 0 ? 1 : 0;
		int oDiameter = size - border * 2;
		thickness -= thickness % 2 == 0 ? 1 : 0;
		int diameter = oDiameter - thickness / 2 - 2 * border;
		int centerOffsetX = getWidth() / 2;
		int centerOffsetY = getHeight() / 2;

		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(Color.black);
		g2d.fillOval((int) (centerOffsetX - oDiameter / 2), (int) (centerOffsetY - oDiameter / 2), (int) (oDiameter),
				(int) (oDiameter));
		g2d.setColor(Color.pink);
		g2d.setStroke(new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
		g2d.drawOval(centerOffsetX - diameter / 2, centerOffsetY - diameter / 2, diameter, diameter);
		String time = getResidualTime();
		if (secret) {

			centiSeconds = centiSeconds % 360;
			time = "??:??";

			g2d.setColor(Color.black);
			g2d.setStroke(new BasicStroke((float) (thickness * 1.5), BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
			g2d.drawArc(centerOffsetX - diameter / 2, centerOffsetY - diameter / 2, diameter, diameter,
					(int) (centiSeconds + 90), 330);
		} else {
			g2d.setColor(Color.black);
			g2d.setStroke(new BasicStroke((float) (thickness * 1.5), BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
			g2d.drawArc(centerOffsetX - diameter / 2, centerOffsetY - diameter / 2, diameter, diameter, 90,
					360 - getProgress());
		}
		g2d.setColor(Color.white);

		float s = size / (time.length() > 4 ? time.length() : 4);
		g2d.setFont(g2d.getFont().deriveFont(s));
		FontMetrics fm = g2d.getFontMetrics();
		int x = centerOffsetX - fm.stringWidth(time) / 2;
		int y = centerOffsetY - fm.getHeight() / 2 + fm.getAscent();
		g2d.drawString(time, x, y);
		g2d.dispose();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JFrame frame = new JFrame();
				frame.setSize(500, 500);
				Timer timer = new Timer(90);
				timer.setSecret(true);
				frame.add(timer, BorderLayout.CENTER);
				frame.setLocationRelativeTo(null);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});
	}

	public void cancel() {
		flag=true;
	}

	boolean secret;

	public void setSecret(boolean secret) {
		this.secret = secret;
	}

}
