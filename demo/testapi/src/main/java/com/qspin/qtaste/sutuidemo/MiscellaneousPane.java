package com.qspin.qtaste.sutuidemo;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

class MiscellaneousPane extends JPanel {

	public MiscellaneousPane()
	{
		super();
		genUI();
	}
	
	private void genUI()
	{
		setLayout(new GridLayout(2,1));
		add(mButton);
		mButton.setName("VISIBILITY_BUTTON");
		mButton.addActionListener(new MyAction());
		add(mSecondContainer);
		mSecondContainer.setBackground(Color.red);
		mSecondContainer.add(mText);
		mText.setName("VISIBILITY_TEXT");
	}
	
	private JButton mButton = new JButton("Click on me");
	private JPanel mSecondContainer = new JPanel();
	private JTextField mText = new JTextField(30);
	
	public static final String COMPONENT_NAME = "MISCEALLANEOUS";
	
	private class MyAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			mSecondContainer.setVisible(!mSecondContainer.isVisible());
			MiscellaneousPane.this.invalidate();
			MiscellaneousPane.this.validate();
		}
	}
}
