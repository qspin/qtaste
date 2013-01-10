package com.qspin.qtaste.controlscriptbuilder.ui.editor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.qspin.qtaste.controlscriptbuilder.model.ControlAction;
import com.qspin.qtaste.controlscriptbuilder.ui.model.ControlActionTableModel;
import com.qspin.qtaste.util.Environment;

@SuppressWarnings("serial")
public class Editor extends JDialog {

	public Editor()
	{
		super(Environment.getEnvironment().getMainFrame(), "Process parameters", true);
		genUI();
		pack();
	}
	
	public void loadControlAction(ControlAction action)
	{
		((ControlActionTableModel)mTable.getModel()).loadControlAction(action);
	}
	
	private void genUI()
	{
		FormLayout layout = new FormLayout("3dlu, right:pref:grow, 3dlu, left:pref:grow, 3dlu", "3dlu, pref:grow, 3dlu, pref, 3dlu");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		
		mTable = new JTable(new ControlActionTableModel());
		builder.add(new JScrollPane(mTable), cc.xyw(2,2,3));
		
		ButtonAction ba = new ButtonAction();
		mSave = new JButton("Save");
		mSave.setEnabled(false);
		mSave.addActionListener(ba);
		builder.add(mSave, cc.xy(2,4));
		
		mCancel = new JButton("Cancel");
		mCancel.addActionListener(ba);
		builder.add(mCancel, cc.xy(4,4));
		
		setLayout(new BorderLayout());
		add(builder.getPanel(), BorderLayout.CENTER);
	}
	
	private class ButtonAction implements ActionListener
	{
		public void actionPerformed(ActionEvent pEvt)
		{
			if ( pEvt.getSource() == mCancel )
			{
				Editor.this.dispose();
			} else if ( pEvt.getSource() == mSave )
			{
				//TODO implement parameter saving
				Editor.this.dispose();
			}
		}
	}
	
	protected JTable mTable;
	protected JButton mCancel;
	protected JButton mSave;
}
