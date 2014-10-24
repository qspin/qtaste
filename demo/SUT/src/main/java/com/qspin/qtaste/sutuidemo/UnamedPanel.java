package com.qspin.qtaste.sutuidemo;

import java.awt.BorderLayout;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

final class UnamedPanel extends JPanel {

	public UnamedPanel()
	{
		super();
		
		genUI();
	}
	
	
	private void genUI()
	{
		prepareComponent();
		FormLayout layout = new FormLayout("3dlu:grow, right:pref, 3dlu, pref, 3dlu:grow", 
				   "3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu:grow");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();

		builder.addLabel("JList :", cc.xy(2, 2));
		builder.add(mList, cc.xy(4, 2));
		builder.addLabel("JSpinner :", cc.xy(2, 4));
		builder.add(mSpinner, cc.xy(4, 4));
		builder.addLabel("JSlider :", cc.xy(2, 6));
		builder.add(mSlider, cc.xy(4, 6));
		builder.addLabel("JComboBox :", cc.xy(2, 8));
		builder.add(mCombo, cc.xy(4, 8));

		setLayout(new BorderLayout());
		add(builder.getPanel(), BorderLayout.CENTER);
	}
	
	private void prepareComponent()
	{
		//do not set name to components
	}

	private JList mList = new JList(new String[]{"listItem_01", "listItem_02", "listItem_03", "listItem_04", "listItem_05"});
	private JSpinner mSpinner = new JSpinner();
	private JSlider mSlider = new JSlider();
	private JComboBox mCombo = new JComboBox(new String[]{"elmt_01","elmt_02","elmt_03","elmt_04"});
	
	private static final int NUMBER_OF_COMPONENT = 4;
	
}
