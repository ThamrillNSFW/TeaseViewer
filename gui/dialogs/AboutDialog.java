package gui.dialogs;

import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import system.TeaseViewer;

public class AboutDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	ImageIcon logo;

	JLabel topLabel;
	JLabel bottomLabel;
	JLabel versionLabel;
	JPanel buttonPanel;

	public AboutDialog(Window owner, TeaseViewer app) {
		super(owner);
		setLayout(new GridBagLayout());
		if (logo == null) {
			try {
				logo = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("imgs/logo.png")));
				topLabel = new JLabel(logo);
			} catch (Exception e) {
				topLabel = new JLabel("Tease viewer");
				topLabel.setFont(topLabel.getFont().deriveFont(20f));
				topLabel.setHorizontalAlignment(SwingConstants.CENTER);
			}
		} else {
			topLabel = new JLabel(logo);
		}
		JButton okButton = new JButton("Ok");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		String defaultMessage = "<html>Developed by Thamrill (thamrill@gmail.com)<br/>If you like this software, please consider supporting me</html>";
		bottomLabel = new JLabel(defaultMessage);
		versionLabel = new JLabel("TeaseViewer, ver " + TeaseViewer.getVersion());
		File additionalInfo = new File(app.getDataFolder(), "info");
		Pattern pattern;
		Matcher matcher;
		if (additionalInfo.exists()) {
			String text = null;
			try (FileReader fr = new FileReader(additionalInfo); BufferedReader br = new BufferedReader(fr)) {
				text = br.readLine();
				pattern = Pattern.compile("#Latest version:(.+?)#");
				matcher = pattern.matcher(text);
				if (matcher.find()) {
					text = matcher.replaceFirst("");
					if (!TeaseViewer.getVersion().equalsIgnoreCase(matcher.group(1))) {
						versionLabel = new JLabel("<html>TeaseViewer, ver " + TeaseViewer.getVersion() + " (latest:"
								+ matcher.group(1) + ")</html>");
					}
				}

				pattern = Pattern.compile("#Button:(.+?)#");
				matcher = pattern.matcher(text);
				while (matcher.find()) {
					if (buttonPanel == null) {
						buttonPanel = new JPanel();
					}
					text = matcher.replaceFirst("");
					
					String[] strs=matcher.group(1).split("uri:");
					if(Desktop.isDesktopSupported()&&strs.length>1) {
						JButton jButton = new JButton(strs[0]);
						jButton.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								try {
									Desktop.getDesktop().browse(new URI(strs[1]));
								} catch (IOException | URISyntaxException e1) {
									e1.printStackTrace();
								}
							}
						});
						buttonPanel.add(jButton);
					}else {
						JLabel label=new JLabel(matcher.group(1).replace("uri:", ":"));
						buttonPanel.add(label);
					}
					matcher=pattern.matcher(text);
				}

				bottomLabel.setText(text);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		add(topLabel, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(1, 1, 1, 1), 0, 0));
		add(bottomLabel, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(1, 1, 1, 1), 0, 0));
		add(versionLabel, new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(1, 1, 1, 1), 0, 0));
		if(buttonPanel!=null) {
			add(buttonPanel, new GridBagConstraints(0, 3, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
					new Insets(5, 1, 1, 1), 0, 0));
		}else {
			
		}
		add(okButton, new GridBagConstraints(0, 4, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets(5, 1, 1, 1), 0, 0));
		pack();
		setLocationRelativeTo(owner);
		setVisible(true);
	}
}
