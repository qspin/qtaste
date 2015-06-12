package com.qspin.qtaste.sutuidemo;

import java.awt.BorderLayout;
import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

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

		FormLayout layout = new FormLayout("3dlu:grow, right:pref, 3dlu, pref, 3dlu:grow", 
										   "3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu:grow");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		
		builder.addLabel("JTextField :", cc.xy(2,2));
		builder.add(mTextField, cc.xy(4,2));
		builder.addLabel("JFormattedTextField :", cc.xy(2,4));
		builder.add(mFormattedTextField, cc.xy(4,4));
		builder.addLabel("JPasswordField :", cc.xy(2,6));
		builder.add(mPasswordField, cc.xy(4,6));
		builder.addLabel("JTextArea :", cc.xy(2,8));
		builder.add(new JScrollPane(mTextArea), cc.xy(4,8));

		setLayout(new BorderLayout());
		add(builder.getPanel(), BorderLayout.CENTER);
	}
	
	private void prepareComponent()
	{
		mTextField.setName("TEXT_FIELD");
		mTextField.setToolTipText("ToolTip from the JTextField");
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
