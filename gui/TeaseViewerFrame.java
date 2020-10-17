package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TreeMap;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileFilter;

import gui.dialogs.AboutDialog;
import gui.dialogs.OptionsDialog;
import gui.dialogs.TeaseDownloaderDialog;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaView;
import system.TeaseViewer;

public class TeaseViewerFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	TreeMap<String, AbstractAction> actions;
	private boolean Am_I_In_FullScreen = false;
	private int PrevX, PrevY, PrevWidth, PrevHeight;
	MouseMotionListener mouseMotionListener;
	JPanel backGroundPanel;
	JTextField notification;
	private TeaseViewer app;
	JFXPanel fxPanel;

	public TeaseViewerFrame(TeaseViewer app) {
		this.app = app;
		app.setTeaseViewerFrame(this);
		actions = new TreeMap<>();
		setIconImage(app.getIcon("teaseviewer").getImage());
		setJMenuBar(createMenuBar());
		setSize(1080, 768);
		setLocationRelativeTo(null);
		mouseMotionListener = new MouseMotionAdapter() {

			@Override
			public void mouseMoved(MouseEvent e) {
				if (e.getY() < 10) {
					getJMenuBar().setVisible(true);
					notification.setVisible(true);
				} else {
					getJMenuBar().setVisible(false);
					notification.setVisible(false);
				}
			}
		};
		notification = new JTextField();
		notification.setEditable(false);
		notification.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		add(notification, BorderLayout.PAGE_END);
		fxPanel = new JFXPanel();
		add(fxPanel, BorderLayout.LINE_START);
		fxPanel.setVisible(false);
		javafx.application.Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				StackPane layout = new StackPane();
				Scene scene=new Scene(layout);
				fxPanel.setScene(scene);
			}
		});
		backGroundPanel = new JPanel(new BorderLayout());
		backGroundPanel.setBackground(Color.black);
		add(backGroundPanel, BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				onClosing();
			}
		});
		backGroundPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F11"), "fullScreen");
		backGroundPanel.getActionMap().put("fullScreen", actions.get("view.fullScreen"));
	}

	public void onClosing() {
		try (FileOutputStream fos = new FileOutputStream(new File(app.getApplicationFolder(), "preferences"));
				ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			oos.writeObject(app.getParameters());
		} catch (Exception e) {
			e.printStackTrace();
		}
		app.clearTempFolder(false);
		System.exit(0);
	}

	public JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(createFileMenu());
		menuBar.add(createEditMenu());
		menuBar.add(createViewMenu());
		menuBar.add(createHelpMenu());
		menuBar.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentHidden(ComponentEvent e) {
				javax.swing.MenuSelectionManager.defaultManager().clearSelectedPath();
			}
		});
		return menuBar;
	}

	private JMenu createFileMenu() {
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		AbstractAction openTease = new AbstractAction("Open Tease...", app.getIcon("file.opentease")) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser() {
					private static final long serialVersionUID = 1L;

					@Override
					public void updateUI() {
						putClientProperty("FileChooser.useShellFolder", Boolean.FALSE);
						super.updateUI();
					}
				};
				if (app.getParameters().containsKey("teasesFolder")
						&& (app.getParameters().get("teasesFolder") instanceof String)) {
					fc.setCurrentDirectory(new File((String) app.getParameters().get("teasesFolder")));
				}
				fc.setAcceptAllFileFilterUsed(false);
				fc.setFileFilter(new FileFilter() {

					@Override
					public String getDescription() {
						return "Tease file (*.tease)";
					}

					@Override
					public boolean accept(File f) {
						return f.isDirectory() || f.getName().toLowerCase().endsWith(".tease");
					}
				});
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.setMultiSelectionEnabled(false);
				if (fc.showOpenDialog(SwingUtilities.getWindowAncestor(fileMenu)) == JFileChooser.APPROVE_OPTION) {
					app.createTeaseManager(fc.getSelectedFile());
				}
			}
		};
		JMenuItem jmi = new JMenuItem(openTease);
		jmi.setAccelerator(KeyStroke.getKeyStroke('O', java.awt.event.InputEvent.CTRL_DOWN_MASK));
		jmi.setMnemonic('O');
		fileMenu.add(jmi);

		AbstractAction downloadTease = new AbstractAction("Download Tease...", app.getIcon("file.downloadtease")) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				new TeaseDownloaderDialog(TeaseViewerFrame.this, app);
			}
		};
		jmi = new JMenuItem(downloadTease);
		jmi.setMnemonic('D');
		fileMenu.add(jmi);

		AbstractAction loadState = new AbstractAction("Load state...") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				File dir = app.getLastSaveFile();
				if (dir == null) {
					dir = app.getTeaseManager().getTeaseFile().getParentFile();
				} else {
					dir = dir.getParentFile();
				}
				JFileChooser jfc = new JFileChooser(dir);
				jfc.setFileFilter(new FileFilter() {

					@Override
					public String getDescription() {
						return "State containing file(*.tease, *.teaseState)";
					}

					@Override
					public boolean accept(File f) {
						return f.isDirectory() || f.getName().endsWith(".tease") || f.getName().endsWith(".teaseState");
					}
				});
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				jfc.setMultiSelectionEnabled(false);
				if (jfc.showOpenDialog(TeaseViewerFrame.this) == JFileChooser.APPROVE_OPTION) {
					app.loadState(jfc.getSelectedFile());
					report("State loaded");
				}
			}
		};
		AbstractAction saveState = new AbstractAction("Save state...") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				File dir = app.getLastSaveFile();
				if (dir == null) {
					dir = app.getTeaseManager().getTeaseFile().getParentFile();
				} else {
					dir = dir.getParentFile();
				}
				JFileChooser jfc = new JFileChooser(dir);
				jfc.setFileFilter(new FileFilter() {

					@Override
					public String getDescription() {
						return "State containing file(*.tease, *.teaseState)";
					}

					@Override
					public boolean accept(File f) {
						return f.isDirectory() || f.getName().endsWith(".tease") || f.getName().endsWith(".teaseState");
					}
				});
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				jfc.setMultiSelectionEnabled(false);
				if (jfc.showSaveDialog(TeaseViewerFrame.this) == JFileChooser.APPROVE_OPTION) {
					app.saveState(jfc.getSelectedFile());
					report("State saved");
				}
			}
		};
		AbstractAction quickSaveState = new AbstractAction("Quick save state") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (app.getLastSaveFile() != null) {
					app.saveState(app.getLastSaveFile());
					report("Quicksave");
				}
			}
		};

		jmi = new JMenuItem(quickSaveState);
		jmi.setAccelerator(KeyStroke.getKeyStroke("F12"));
		jmi.setMnemonic('k');

		AbstractAction quit = new AbstractAction("Quit") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				onClosing();
			}
		};

		fileMenu.addSeparator();
		fileMenu.add(new JMenuItem(loadState));
		fileMenu.add(new JMenuItem(saveState));
		fileMenu.add(jmi);
		fileMenu.addSeparator();
		fileMenu.add(new JMenuItem(quit));

		actions.put("file.openTease", openTease);
		actions.put("file.saveState", saveState);
		actions.put("file.quickSaveState", quickSaveState);
		actions.put("file.loadState", loadState);
		actions.put("file.quit", quit);

		return fileMenu;
	}

	private JMenu createEditMenu() {
		JMenu editMenu = new JMenu("Edit");
		editMenu.setMnemonic('E');
		AbstractAction preferences = new AbstractAction("Preferences...", app.getIcon("edit.options")) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				new OptionsDialog(TeaseViewerFrame.this, app);
			}
		};
		JMenuItem jmi = new JMenuItem(preferences);
		jmi.setMnemonic('P');
		editMenu.add(jmi);
		actions.put("edit.preferences", preferences);
		return editMenu;
	}

	private JMenu createHelpMenu() {
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('H');
		AbstractAction guide = new AbstractAction("Guide", app.getIcon("help.guide")) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().open(new File(app.getDataFolder(), "readme.txt"));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		};
		AbstractAction about = new AbstractAction("About", app.getIcon("teaseviewer")) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				new AboutDialog(TeaseViewerFrame.this, app);
			}
		};
		AbstractAction openLog = new AbstractAction("Open log") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().open(app.getLogger().getLogFile());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		};
		if (!Desktop.isDesktopSupported()) {
			guide.setEnabled(false);
			openLog.setEnabled(false);
		}
		actions.put("help.about", about);
		actions.put("help.guide", guide);
		actions.put("help.openLog", openLog);

		JMenuItem jmi = new JMenuItem(about);
		jmi.setMnemonic('A');
		helpMenu.add(jmi);

		jmi = new JMenuItem(guide);
		jmi.setMnemonic('G');
		helpMenu.add(jmi);

		jmi = new JMenuItem(openLog);
		if (!Desktop.isDesktopSupported()) {
			jmi.setEnabled(false);
		}
		jmi.setMnemonic('l');
		helpMenu.add(jmi);

		return helpMenu;
	}

	private JMenu createViewMenu() {
		JMenu viewMenu = new JMenu("View");
		viewMenu.setMnemonic('V');
		AbstractAction fullScreen = new AbstractAction("FullScreen") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (Am_I_In_FullScreen == false) {
					PrevX = getX();
					PrevY = getY();
					PrevWidth = getWidth();
					PrevHeight = getHeight();
					getJMenuBar().setVisible(false);
					notification.setVisible(false);
					backGroundPanel.addMouseMotionListener(mouseMotionListener);
					dispose();
					setUndecorated(true);
					int[] pos = getX0ForScreen();
					setBounds(pos[0], 0, pos[1], pos[2]);
					setVisible(true);
					Am_I_In_FullScreen = true;
				} else {
					setVisible(true);
					setBounds(PrevX, PrevY, PrevWidth, PrevHeight);
					getJMenuBar().setVisible(true);
					notification.setVisible(true);
					backGroundPanel.removeMouseMotionListener(mouseMotionListener);
					dispose();
					setUndecorated(false);
					setVisible(true);
					Am_I_In_FullScreen = false;
				}
			}
		};
		actions.put("view.fullScreen", fullScreen);

		JMenuItem jmi = new JMenuItem(fullScreen);
		jmi.setMnemonic('F');
		jmi.setAccelerator(KeyStroke.getKeyStroke("F11"));
		viewMenu.add(jmi);

		return viewMenu;
	}

	public int[] getX0ForScreen() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		int x0 = 0;
		int x1 = (int) getBounds().getMinX();
		for (int i = 0; i < gs.length; i++) {
			DisplayMode dm = gs[i].getDisplayMode();
			if (x1 < x0 + dm.getWidth()) {
				return new int[] { x0, dm.getWidth(), dm.getHeight() };
			} else {
				x0 += dm.getWidth();
			}
		}
		return new int[] { x0, getToolkit().getScreenSize().width, getToolkit().getScreenSize().height };
	}

	Thread timerThread;
	Color targetColor;

	public void report(String msg) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		notification.setForeground(Color.black);
		LocalDateTime now = LocalDateTime.now();
		if (timerThread != null && timerThread.isAlive()) {
			timerThread.interrupt();
		}
		if (targetColor == null) {
			targetColor = notification.getBackground();
		}
		notification.setText(dtf.format(now) + ": " + msg);
		timerThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					for (int ii = 0; ii < 20; ii++) {
						Thread.sleep(200);
						float val = ii * 1.0f / 20;
						notification.setForeground(new Color((int) (val * targetColor.getRed()),
								(int) (val * targetColor.getGreen()), (int) (val * targetColor.getBlue())));
					}
					Thread.sleep(100);
					notification.setText("");
				} catch (InterruptedException e) {
				}
			}
		});
		timerThread.start();
	}

	public void setPanel(JPanel panel) {
		if (backGroundPanel.getComponentCount() > 0) {
			backGroundPanel.removeAll();
		}
		backGroundPanel.add(panel, BorderLayout.CENTER);
		backGroundPanel.updateUI();
		backGroundPanel.repaint();
	}

	public void putMediaView(MediaView mv) {
		((StackPane)fxPanel.getScene().getRoot()).getChildren().add(mv);
	}
}
