package com.qspin.qtaste.sutuidemo;

import java.awt.GridLayout;
import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

final class DocumentPanel extends JPanel {

	public DocumentPanel()
	{
		super();
		setName(COMPONENT_NAME);
		
		genUI();
	}
	
	
	private void genUI()
	{
		prepareComponent();
		setLayout(new GridLayout(NUMBER_OF_COMPONENT,2, 5, 5));
		add(new JLabel("JTextField :"));
		add(mTextField);
		add(new JLabel("JFormattedTextField :"));
		add(mFormattedTextField);
		add(new JLabel("JPasswordField :"));
		add(mPasswordField);
		add(new JLabel("JTextArea :"));
		add(new JScrollPane(mTextArea));
	}
	
	private void prepareComponent()
	{
		mTextField.setName("TEXT_FIELD");
		mFormattedTextField.setName("FORMATTED_TEXT_FIELD");
		mPasswordField.setName("PASSWORD_FIELD");
		mTextArea.setName("TEXT_AREA");
	}

	private JTextField mTextField = new JTextField();
	private JFormattedTextField mFormattedTextField = new JFormattedTextField(NumberFormat.getPercentInstance());
	private JPasswordField mPasswordField = new JPasswordField();
	private JTextArea mTextArea = new JTextArea(5,30);
	private static final int NUMBER_OF_COMPONENT = 4;
	public static final String COMPONENT_NAME = "DOCUMENT_PANEL";
}
