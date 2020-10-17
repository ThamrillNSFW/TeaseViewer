package gui.dialogs;

import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import system.TeaseViewer;

public class EndDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	JButton goTeaseButton;
	JButton goAuthorButton;
	JButton goAuthorTeasesButton;
	JButton okButton;

	public EndDialog(Window owner, TreeMap<String, String> teaseData, TeaseViewer app) {
		super(owner);
		setTitle(teaseData.get("teaseTitle") + " by " + teaseData.get("teaseAuthor"));
		goTeaseButton = new JButton("Open tease", app.getIcon("browser_small"));
		goAuthorButton = new JButton("Open author's page", app.getIcon("browser_small"));
		goAuthorTeasesButton = new JButton("Open tease list for author", app.getIcon("browser_small"));
		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
			goTeaseButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						Desktop.getDesktop().browse(new URI("https://milovana.com/webteases/showtease.php?id="+teaseData.get("teaseID")));
					} catch (IOException | URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
			});
			goAuthorButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						Desktop.getDesktop().browse(new URI("https://milovana.com/forum/memberlist.php?mode=viewprofile&u="+teaseData.get("authorID")));
					} catch (IOException | URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
			});
			goAuthorTeasesButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						Desktop.getDesktop().browse(new URI("https://milovana.com/webteases/?author="+teaseData.get("authorID")));
					} catch (IOException | URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
			});
		} else {
			goTeaseButton.setEnabled(false);
			goAuthorButton.setEnabled(false);
			goAuthorTeasesButton.setEnabled(false);
		}
		okButton=new JButton("Close");
		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		setLayout(new GridBagLayout());
		JLabel label=new JLabel("You completed the tease, consider these options.");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		label.setFont(label.getFont().deriveFont(11f));
		add(label, new GridBagConstraints(0, 0, 2, 1, 2, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0,0));
		add(goTeaseButton, new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0,0));
		add(goAuthorButton, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0,0));
		add(goAuthorTeasesButton, new GridBagConstraints(0, 2, 2, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0,0));
		
		add(okButton, new GridBagConstraints(0, 3, 2, 1, 2, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0));
		pack();
		setLocationRelativeTo(owner);
		setVisible(true);
	}
}
