package com.qspin.qtaste.sutuidemo;

import java.awt.BorderLayout;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

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
		
		FormLayout layout = new FormLayout("3dlu:grow, right:pref, 3dlu, pref, 3dlu:grow", 
										   "3dlu,pref, 3dlu, pref, 3dlu, pref, 3dlu:grow");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		
		builder.addLabel("JCheckBox :", cc.xy(2,2));
		builder.add(mCheck, cc.xy(4, 2));
		builder.addLabel("JRadioButton :", cc.xy(2, 4));
		builder.add(mRadio, cc.xy(4, 4));
		builder.addLabel("JToggleButton :", cc.xy(2, 6));
		builder.add(mToggle, cc.xy(4, 6));
		
		setLayout(new BorderLayout());
		add(builder.getPanel(), BorderLayout.CENTER);
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
