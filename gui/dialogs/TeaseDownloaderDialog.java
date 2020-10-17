package gui.dialogs;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.TreeMap;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import system.Logger;
import system.TeaseViewer;
import system.downloader.TeaseDownloader;

public class TeaseDownloaderDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	Thread t;
	AbstractAction clearAction;
	AbstractAction downloadAction;
	TeaseDownloader td;
	JTextArea logger;

	public TeaseDownloaderDialog(JFrame parent, TeaseViewer app) {
		setTitle("Download tease");
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setIconImage(app.getIcon("file.downloadtease").getImage());
		setModal(true);
		JPanel panel = new JPanel();
		logger = new JTextArea(5, 20);
		logger.setLineWrap(true);
		logger.setWrapStyleWord(true);
		logger.setEditable(false);
		TreeMap<String, Object> parameters = app.getParameters();
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		JLabel label = new JLabel("Tease url");
		DefaultBoundedRangeModel dbrm = new DefaultBoundedRangeModel(0, 1, 0, 100);
		JProgressBar progressBar = new JProgressBar(dbrm);

		JTextField urlField = new JTextField(30);
		JButton browseButton = new JButton();
		JButton downloadButton = new JButton();
		JButton okButton = new JButton();
		JCheckBox ignoreErrors = new JCheckBox("Ignore errors");
		JCheckBox collectByAuthor = new JCheckBox("Collect by author");
		if (parameters.containsKey("ignoreErrors")
				&& (parameters.get("ignoreErrors") instanceof Boolean)) {
			ignoreErrors.setSelected((boolean) parameters.get("ignoreErrors"));
		}
		if (parameters.containsKey("cbaDownloadedTeases")
				&& (parameters.get("cbaDownloadedTeases") instanceof Boolean)) {
			collectByAuthor.setSelected((boolean) parameters.get("cbaDownloadedTeases"));
		}
		JTextField targetField = new JTextField();
		JTextField thumbnailField = new JTextField();
		if (parameters.containsKey("teasesFolder") && (parameters.get("teasesFolder") instanceof String)) {
			targetField.setText((String) parameters.get("teasesFolder"));
		}
		downloadAction = new AbstractAction("Download") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				downloadButton.setEnabled(false);
				label.setText("Progress");
				targetField.setEnabled(false);
				browseButton.setEnabled(false);
				ignoreErrors.setEnabled(false);
				collectByAuthor.setEnabled(false);
				thumbnailField.setEnabled(false);
				panel.remove(urlField);
				dbrm.setValue(0);
				panel.add(progressBar, new GridBagConstraints(1, 0, 2, 1, 1, 0, GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
				panel.updateUI();
				panel.repaint();
				progressBar.setStringPainted(true);
				td = new TeaseDownloader(app) {
					@Override
					public void updateProgress(float progress) {
						dbrm.setValue((int) (progress * 100));
					}

					@Override
					public void onCompletion() {
						progressBar.setString("Completed");
						okButton.setText("Ok");
						downloadButton.setAction(clearAction);
						downloadButton.setEnabled(true);
					}

					@Override
					public void report(String message, int severity) {
						String str=message;
						if (severity == TeaseDownloader.ERROR) {
							if(!ignoreErrors.isSelected()) {
								JOptionPane.showMessageDialog(TeaseDownloaderDialog.this, message, "Error",
										JOptionPane.ERROR_MESSAGE);
							}
							str="##\t"+str;
						}else if (severity == TeaseDownloader.WARNING) {
							str="!\t"+str;
						}
						

						if (logger.getText().isBlank()) {
							logger.append(str);
						} else {
							logger.append("\n" + str);
						}
						logger.setCaretPosition(logger.getText().length() - 1);
					}

					@Override
					public int requestInput(String str) {
						final String[] options = new String[] { "Cancel", "Overwrite", "Rename" };
						String operation = (String) JOptionPane.showInputDialog(SwingUtilities.getWindowAncestor(panel),
								str, "Tease already exists", JOptionPane.PLAIN_MESSAGE, null, options, options[1]);

						if (operation == null) {
							return TeaseDownloader.CANCEL;
						}
						switch (operation) {
						case "Cancel":
							return TeaseDownloader.CANCEL;
						case "Overwrite":
							return TeaseDownloader.OVERWRITE;
						case "Rename":
							return TeaseDownloader.RENAME;
						default:
							return TeaseDownloader.CANCEL;
						}
					}
				};
				td.setCba(collectByAuthor.isSelected());
				td.setZip(ignoreErrors.isSelected());
				td.setTarget(targetField.getText());
				td.setSourceUrl(urlField.getText());
				td.setMaxRetries(5);
				if (!thumbnailField.getText().isBlank()) {
					td.setThumbnailURL(thumbnailField.getText());
				}
				t = new Thread(td);
				t.start();
			}
		};
		clearAction = new AbstractAction("Clear") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				downloadButton.setAction(downloadAction);
				label.setText("Tease url");
				targetField.setEnabled(true);
				thumbnailField.setEnabled(true);
				browseButton.setEnabled(true);
				ignoreErrors.setEnabled(true);
				collectByAuthor.setEnabled(true);
				panel.remove(progressBar);
				progressBar.setString(null);
				logger.setText("");
				panel.add(urlField, new GridBagConstraints(1, 0, 2, 1, 1, 0, GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
				okButton.setText("Cancel");
				panel.updateUI();
				panel.repaint();
			}
		};
		downloadButton.setAction(downloadAction);

		browseButton.setAction(new AbstractAction("Browse...") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(targetField.getText());
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.setMultiSelectionEnabled(false);
				if (fc.showOpenDialog(TeaseDownloaderDialog.this) == JFileChooser.APPROVE_OPTION) {
					targetField.setText(fc.getSelectedFile().getAbsolutePath());
					downloadButton.setEnabled(!urlField.getText().isBlank());
				}
			}
		});
		urlField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				check();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				check();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				check();
			}

			private void check() {
				downloadButton.setEnabled(!urlField.getText().isBlank() && !targetField.getText().isBlank());
			}
		});
		okButton.setAction(new AbstractAction("Cancel") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (t != null && t.isAlive()) {
					td.stop();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					t.interrupt();
				}
				if (!targetField.getText().isBlank()) {
					app.getParameters().put("teasesFolder", targetField.getText());
				}
				dispose();
			}
		});

		if (app.getParameters().containsKey("teasesFolder")) {
			targetField.setText(app.getParameters().get("teasesFolder").toString());
		}

		downloadButton.setEnabled(false);

		panel.setLayout(new GridBagLayout());
		Insets insets = new Insets(1, 1, 1, 1);
		int y = 0;
		panel.add(label, new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.LINE_END, GridBagConstraints.NONE,
				insets, 0, 0));
		panel.add(urlField, new GridBagConstraints(1, y, 2, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		panel.add(downloadButton, new GridBagConstraints(3, y, 1, 1, 0, 0, GridBagConstraints.LINE_START,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		y++;
		panel.add(new JLabel("Folder"), new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.LINE_END,
				GridBagConstraints.NONE, insets, 0, 0));
		panel.add(targetField, new GridBagConstraints(1, y, 2, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		panel.add(browseButton, new GridBagConstraints(3, y, 1, 1, 0, 0, GridBagConstraints.LINE_START,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		y++;
		panel.add(new JLabel("Thumbnail URL"), new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.LINE_END,
				GridBagConstraints.NONE, insets, 0, 0));
		panel.add(thumbnailField, new GridBagConstraints(1, y, 2, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		panel.add(new JLabel("(Optional)"), new GridBagConstraints(3, y, 1, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, insets, 0, 0));
		y++;
		panel.add(ignoreErrors, new GridBagConstraints(0, y, 2, 1, 1, 0, GridBagConstraints.LINE_END,
				GridBagConstraints.NONE, insets, 0, 0));
		panel.add(collectByAuthor, new GridBagConstraints(2, y, 2, 1, 1, 0, GridBagConstraints.LINE_START,
				GridBagConstraints.NONE, insets, 0, 0));
		y++;
		panel.add(new JScrollPane(logger), new GridBagConstraints(0, y, 4, 1, 1, 3, GridBagConstraints.LINE_START,
				GridBagConstraints.BOTH, insets, 0, 0));
		y++;
		panel.add(okButton, new GridBagConstraints(0, y, 4, 1, 4, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets(10, 0, 0, 0), 0, 0));

		add(panel, BorderLayout.CENTER);

		pack();
		setLocationRelativeTo(parent);
		setVisible(true);
	}
}
