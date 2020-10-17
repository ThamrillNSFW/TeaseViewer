package gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import system.Logger;
import system.TeaseViewer;

public class OptionsDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	JList<String> categories;
	JButton okButton;
	JButton cancelButton;
	JPanel mainPanel;
	JSpinner maximumRetries;
	JSpinner defaultFontSize;
	JButton clearTempsButton;
	JLabel tempsCount;
	JButton openLogsButton;
	JLabel logsCount;

	public OptionsDialog(Component parent, TeaseViewer app) {
		setLayout(new BorderLayout());
		setTitle("Preferences");
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setModal(true);
		setIconImage(app.getIcon("edit.options").getImage());
		TreeMap<String, Object> parameters = app.getParameters();
		teasesFolderField = new JTextField(30);
//		emojiStyleCB = new JComboBox<>(new DefaultComboBoxModel<>(new String[] { "Apple", "Google", "Whatsapp", "Samsung", "OpenMoji", "Twemoji", "none" }));
		emojiStyleCB = new JComboBox<>(new DefaultComboBoxModel<>(new String[] { "Apple","au by KDDI","Docomo","emojidex","Emojipedia","Facebook","Google","HTC","JoyPixels","LG","Messenger","Microsoft","Mozilla","OpenMoji","Samsung","SoftBank","Twitter","WhatsApp" }));
//		emojiStyleCB.setToolTipText("Due to licensing, sets marked with * are not provided with the program");
		loggerPriorityCB = new JComboBox<>(
				new DefaultComboBoxModel<>(new String[] { "All", "Warnings", "Errors only" }));
		consolePriorityCB = new JComboBox<>(
				new DefaultComboBoxModel<>(new String[] { "All", "Warnings", "Errors only" }));
		maximumRetries = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
		if (parameters.containsKey("teasesFolder")) {
			teasesFolderField.setText(parameters.get("teasesFolder").toString());
		}
		if (parameters.containsKey("emojiSet")) {
			emojiStyleCB.getModel().setSelectedItem(parameters.get("emojiSet"));
		} else {
			emojiStyleCB.getModel().setSelectedItem("OpenMoji");
		}
		if (parameters.containsKey("maxTrials") && (parameters.get("maxTrials") instanceof Number)) {
			maximumRetries.setValue(parameters.get("maxTrials"));
		} else {
			maximumRetries.setValue(5);
		}
		defaultFontSize = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
		if (parameters.containsKey("defaultFontSize") && (parameters.get("defaultFontSize") instanceof Number)) {
			defaultFontSize.setValue(parameters.get("defaultFontSize"));
		} else {
			defaultFontSize.setValue(17);
		}

		if (parameters.containsKey("loggerPriority") && (parameters.get("loggerPriority") instanceof Number)) {
			loggerPriorityCB.setSelectedIndex((int) parameters.get("loggerPriority"));
		} else {
			loggerPriorityCB.setSelectedIndex(1);
		}

		if (parameters.containsKey("consolePriority")&& (parameters.get("consolePriority") instanceof Number)) {
			consolePriorityCB.setSelectedIndex((int) parameters.get("consolePriority"));
		} else {
			consolePriorityCB.setSelectedIndex(2);
		}

		String[] names = app.getDataFolder().list();
		int count = -1;
		Pattern pattern = Pattern.compile("temp......");
		Matcher matcher;
		for (String name : names) {
			matcher = pattern.matcher(name);
			if (matcher.find()) {
				count++;
			}
		}
		clearTempsButton = new JButton("Clean");
		clearTempsButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				app.clearTempFolder(true);
				String[] names = app.getDataFolder().list();
				int count = -1;
				Pattern pattern = Pattern.compile("temp......");
				Matcher matcher;
				for (String name : names) {
					matcher = pattern.matcher(name);
					if (matcher.find()) {
						count++;
					}
				}
				if (count > 0) {
					tempsCount.setText("There are " + count + " residual temp directories");

				} else {
					tempsCount.setText("There are no residual temp directories");
					clearTempsButton.setEnabled(false);
					tempsCount.setEnabled(false);
				}
				tempsCount.updateUI();
			}
		});
		if (count > 0) {
			tempsCount = new JLabel("There are " + count + " residual temp directories");

		} else {
			tempsCount = new JLabel("There are no residual temp directories");
			clearTempsButton.setEnabled(false);
			tempsCount.setEnabled(false);
		}
		File source = new File(app.getApplicationFolder(), "logs");
		openLogsButton = new JButton("Open folder");
		openLogsButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (Desktop.isDesktopSupported()) {
					try {
						Desktop.getDesktop().open(source);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		if (source.exists() && source.isDirectory()) {
			names = source.list();
			count = 0;
			pattern = Pattern.compile(".+\\.log");
			for (String name : names) {
				matcher = pattern.matcher(name);
				if (matcher.find()) {
					count++;
				}
			}
			if (count > 0) {
				logsCount = new JLabel("There are " + count + " logs");

			} else {
				logsCount = new JLabel("There are no logs");
				logsCount.setEnabled(false);
			}
		}

		if (logsCount == null) {
			logsCount = new JLabel("There are no logs");
			openLogsButton.setEnabled(false);
			openLogsButton.setToolTipText("Logs folder couldn't be found");
		}

		ignoreCheckBox = new JCheckBox("Ignore errors");
		collectByAuthor = new JCheckBox("Collect by author");
		if (parameters.containsKey("ignoreErrors") && (parameters.get("ignoreErrors") instanceof Boolean)) {
			ignoreCheckBox.setSelected((boolean) parameters.get("ignoreErrors"));
		}
		if (parameters.containsKey("cbaDownloadedTeases")
				&& (parameters.get("cbaDownloadedTeases") instanceof Boolean)) {
			collectByAuthor.setSelected((boolean) parameters.get("cbaDownloadedTeases"));
		}

		categories = new JList<>(new String[] { "Directories", "Downloader", "Appearance", "Debugging"});
		categories.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				setCategory(categories.getSelectedValue());
			}
		});
		categories.setSelectedIndex(0);
		add(new JScrollPane(categories), BorderLayout.LINE_START);
		JPanel bottomPanel = new JPanel(new GridBagLayout());
		okButton = new JButton(new AbstractAction("Ok") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				app.getParameters().put("ignoreErrors", ignoreCheckBox.isSelected());
				app.getParameters().put("cbaDownloadedTeases", collectByAuthor.isSelected());
				app.getParameters().put("emojiSet", emojiStyleCB.getSelectedItem().toString());
				app.getParameters().put("teasesFolder", teasesFolderField.getText());
				app.getParameters().put("maxTrials", teasesFolderField.getText());
				app.getParameters().put("defaultFontSize", defaultFontSize.getValue());
				app.getParameters().put("loggerPriority", loggerPriorityCB.getSelectedIndex());
				app.getParameters().put("consolePriority", consolePriorityCB.getSelectedIndex());
				Logger.updateParameters();
				dispose();
			}
		});
		cancelButton = new JButton(new AbstractAction("Cancel") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		bottomPanel.add(okButton, new GridBagConstraints(0, 0, 1, 1, 0.5, 1, GridBagConstraints.LINE_END,
				GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0));
		bottomPanel.add(cancelButton, new GridBagConstraints(1, 0, 1, 1, 0.5, 1, GridBagConstraints.LINE_START,
				GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0));
		add(bottomPanel, BorderLayout.PAGE_END);
		pack();
		setLocationRelativeTo(parent);
		setVisible(true);
	}

	JTextField teasesFolderField;
	JComboBox<String> emojiStyleCB;
	JComboBox<String> loggerPriorityCB;
	JComboBox<String> consolePriorityCB;
	JCheckBox ignoreCheckBox;
	JCheckBox collectByAuthor;

	public void setCategory(String category) {
		if (mainPanel != null) {
			mainPanel.getParent().remove(mainPanel);
		}
		mainPanel = new JPanel(new GridBagLayout());
		Insets defaultInsets = new Insets(1, 1, 1, 1);
		switch (category) {
		case "Directories":
			mainPanel.add(new JLabel("Teases folder"), new GridBagConstraints(0, 0, 1, 1, 0, 0,
					GridBagConstraints.LINE_END, GridBagConstraints.NONE, defaultInsets, 0, 0));
			mainPanel.add(teasesFolderField, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
			mainPanel.add(new BrowseButton(teasesFolderField, JFileChooser.DIRECTORIES_ONLY, false, null, false),
					new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START,
							GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
			mainPanel.add(tempsCount, new GridBagConstraints(0, 1, 2, 1, 0, 0, GridBagConstraints.LINE_END,
					GridBagConstraints.NONE, defaultInsets, 0, 0));
			mainPanel.add(clearTempsButton, new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START,
					GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));

			mainPanel.add(Box.createGlue(), new GridBagConstraints(0, 3, 3, 1, 1, 1, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, defaultInsets, 0, 0));
			break;
		case "Downloader":
			mainPanel.add(ignoreCheckBox, new GridBagConstraints(0, 0, 2, 1, 2, 0, GridBagConstraints.LINE_END,
					GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
			mainPanel.add(collectByAuthor, new GridBagConstraints(2, 0, 2, 1, 2, 0, GridBagConstraints.LINE_END,
					GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
			mainPanel.add(new JLabel("Max retries for media"), new GridBagConstraints(0, 1, 1, 1, 0, 0,
					GridBagConstraints.LINE_START, GridBagConstraints.NONE, defaultInsets, 0, 0));
			mainPanel.add(maximumRetries, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.LINE_END,
					GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
			mainPanel.add(Box.createGlue(), new GridBagConstraints(0, 2, 4, 1, 4, 1, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, defaultInsets, 0, 0));
			break;
		case "Appearance":
			mainPanel.add(new JLabel("Emoji appearance"), new GridBagConstraints(0, 0, 1, 1, 0, 0,
					GridBagConstraints.LINE_END, GridBagConstraints.NONE, defaultInsets, 0, 0));
			mainPanel.add(emojiStyleCB, new GridBagConstraints(1, 0, 3, 1, 3, 0, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));

			mainPanel.add(new JLabel("Font size"), new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.LINE_END,
					GridBagConstraints.NONE, defaultInsets, 0, 0));
			mainPanel.add(defaultFontSize, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));

			mainPanel.add(Box.createGlue(), new GridBagConstraints(2, 1, 2, 1, 2, 0, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
			mainPanel.add(Box.createGlue(), new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, defaultInsets, 0, 0));
			break;
		case "Debugging":
			mainPanel.add(new JLabel("Logger priority"), new GridBagConstraints(0, 0, 1, 1, 1, 0,
					GridBagConstraints.LINE_END, GridBagConstraints.NONE, defaultInsets, 0, 0));
			mainPanel.add(loggerPriorityCB, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
			mainPanel.add(new JLabel("Console priority"), new GridBagConstraints(2, 0, 1, 1, 1, 0,
					GridBagConstraints.LINE_END, GridBagConstraints.NONE, defaultInsets, 0, 0));
			mainPanel.add(consolePriorityCB, new GridBagConstraints(3, 0, 1, 1, 1, 0, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
			mainPanel.add(logsCount, new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.LINE_END,
					GridBagConstraints.NONE, defaultInsets, 0, 0));
			mainPanel.add(openLogsButton, new GridBagConstraints(3, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START,
					GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
			
			mainPanel.add(Box.createGlue(), new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, defaultInsets, 0, 0));
			break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + category);
		}
		add(mainPanel, BorderLayout.CENTER);
		mainPanel.updateUI();
	}

	public static class BrowseButton extends JButton {
		private static final long serialVersionUID = 1L;

		public BrowseButton(JTextField field, int fileSelectionMode, boolean multiselection, FileFilter[] filters,
				boolean acceptAll) {
			setAction(new AbstractAction("Browse...") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser fc = new JFileChooser(field.getText());

					fc.setFileSelectionMode(fileSelectionMode);
					fc.setMultiSelectionEnabled(multiselection);
					if (filters != null) {
						for (FileFilter ff : filters) {
							fc.addChoosableFileFilter(ff);
						}
					}
					fc.setAcceptAllFileFilterUsed(acceptAll);
					int choice = fc.showOpenDialog(SwingUtilities.getRoot(field));
					if (choice == JFileChooser.APPROVE_OPTION) {
						String str = "";
						if (multiselection) {
							for (File f : fc.getSelectedFiles()) {
								str += File.pathSeparator + f.getAbsolutePath();
							}
							field.setText(str.substring(2));
						} else {
							field.setText(fc.getSelectedFile().getAbsolutePath());
						}
					}
				}
			});

		}
	}
}
