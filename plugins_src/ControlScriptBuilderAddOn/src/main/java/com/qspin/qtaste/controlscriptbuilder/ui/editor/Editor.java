package com.qspin.qtaste.controlscriptbuilder.ui.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.qspin.qtaste.controlscriptbuilder.model.ControlAction;
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
		regenPanWithProperties(action);
	}
	
	private void regenPanWithProperties(ControlAction pAction) {
		StringBuilder rowSpecBuilder = new StringBuilder();
		for (@SuppressWarnings("unused") Object key : pAction.getParameters().keySet() )
		{
			rowSpecBuilder.append("3dlu, pref, ");
		}
		FormLayout layout = new FormLayout("3dlu, right:pref:grow, 3dlu, pref:grow, 3dlu", rowSpecBuilder + "3dlu:grow");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		
		int rowIndex = 2;
		Enumeration<?> enu = pAction.getParameters().keys();
		while ( enu.hasMoreElements() )
		{
			String key = enu.nextElement().toString();
			builder.addLabel(key + " : ", cc.xy(2, rowIndex));
			Component comp;
			if (ControlAction.getParameterType(pAction, key) == Boolean.class)
			{
				comp = new JCheckBox("", Boolean.parseBoolean(pAction.getParameters().getProperty(key)));
				comp.setEnabled(false);
			}
			else if (ControlAction.getParameterType(pAction, key) == Integer.class)
			{
				comp = new JFormattedTextField(NumberFormat.getIntegerInstance());
				((JFormattedTextField)comp).setText(pAction.getParameters().getProperty(key));
				((JFormattedTextField)comp).setEditable(false);
			}
			else
			{
				comp = new JTextField(pAction.getParameters().getProperty(key));
				((JTextField)comp).setEditable(false);
			}
			builder.add(comp, cc.xy(4, rowIndex));
			rowIndex += 2;
		}
		
		mPaneWithProperties.removeAll();
		mPaneWithProperties.setLayout(new BorderLayout());
		mPaneWithProperties.add(builder.getPanel(), BorderLayout.CENTER);
		pack();
	}

	private void genUI()
	{
		FormLayout layout = new FormLayout("3dlu, right:pref:grow, 3dlu, left:pref:grow, 3dlu", "3dlu, pref:grow, 3dlu, pref, 3dlu");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		
		mPaneWithProperties = new JPanel();
		builder.add(new JScrollPane(mPaneWithProperties), cc.xyw(2,2,3));
		
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
				Editor.this.dispose();
			}
		}
	}
	
	protected JPanel mPaneWithProperties;
	protected JButton mCancel;
	protected JButton mSave;
}
