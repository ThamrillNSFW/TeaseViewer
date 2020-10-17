package gui.eospanel.bubbles;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

import teaseManagers.EOSTeaseManager;
import teaseManagers.eos.actions.PromptAction;

public class PromptBubble extends InputBubble {
	private static final long serialVersionUID = 1L;
	public PromptBubble(PromptAction action, EOSTeaseManager tm) {
		setLayout(new GridBagLayout());
		float fontSize = ((Number) tm.getApp().getParameters().get("defaultFontSize")).floatValue();
		JTextField textField = new JTextField(10);
		textField.setFont(textField.getFont().deriveFont(fontSize));
		
		textField.setHorizontalAlignment(JTextField.CENTER);
		textField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				tm.reportInput("prompt", textField.getText());
				transform(textField.getText(), true, true, fontSize);
			}
		});
		
		textField.setSelectionColor(new Color(1, 1, 1, 0.8f));
		textField.setSelectedTextColor(Color.black);
		textField.setBackground(new Color(0.1f, 0.1f, 0.1f));
		textField.setForeground(Color.WHITE);
		textField.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		Insets defaultInsets = new Insets(10,10,10,10);
		add(textField, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, defaultInsets, 0, 0));
	}
}
