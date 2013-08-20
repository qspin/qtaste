package com.qspin.qtaste.sutuidemo;

import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;

final class ChoosePanel extends JPanel {

	public ChoosePanel()
	{
		super();
		setName(COMPONENT_NAME);
		
		genUI();
	}
	
	
	private void genUI()
	{
		prepareComponent();
		setLayout(new GridLayout(NUMBER_OF_COMPONENT,2, 5, 5));
		add(new JLabel("JCheckBox :"));
		add(mCheck);
		add(new JLabel("JRadioButton :"));
		add(mRadio);
		add(new JLabel("JToggleButton :"));
		add(mToggle);
	}
	
	private void prepareComponent()
	{
		mCheck.setName("CHECK_BOX");
		mRadio.setName("RADIO_BUTTON");
		mToggle.setName("TOGGLE_BUTTON");
	}

	private JCheckBox mCheck = new JCheckBox("Check box");
	private JRadioButton mRadio = new JRadioButton("Radio");
	private JToggleButton mToggle = new JToggleButton("Toggle", false);
	
	
	private static final int NUMBER_OF_COMPONENT = 3;
	public static final String COMPONENT_NAME = "CHOOSE_PANEL";
}
