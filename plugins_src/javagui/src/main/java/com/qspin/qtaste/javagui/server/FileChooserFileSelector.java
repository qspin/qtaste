package com.qspin.qtaste.javagui.server;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JTextField;

import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

public class FileChooserFileSelector extends UpdateComponentCommander {

	
	@Override
	protected void prepareActions() throws QTasteException {
		m_filePathTextField = getTextField((JFileChooser)this.mFoundComponent);
		if (m_filePathTextField == null)
				throw new QTasteTestFailException("Text field for setting the file path not found!");
		m_okButton = getOkButton((JFileChooser)this.mFoundComponent);
		if (m_okButton == null)
			throw new QTasteTestFailException("Button not found!");
	}

	@Override
	protected void doActionsInSwingThread() throws QTasteException {
		m_filePathTextField.setText(this.mData[0].toString());
		m_okButton.setBorder(BorderFactory.createLineBorder(Color.red, 5));
		m_okButton.doClick();
	}
	
	private JTextField getTextField(Container c)
	{
		for (int i = 0; i < c.getComponentCount(); i++)
		{
			Component comp = c.getComponent(i);
			if ( comp instanceof JTextField )
				return (JTextField)comp;
			else if ( comp instanceof Container )
			{
				JTextField result = getTextField((Container)comp);
				if ( result != null )
					return result;
			}
		}
		return null;
	}
	
	private AbstractButton getOkButton(Container c)
	{
		for (int i = 0; i < c.getComponentCount(); i++)
		{
			Component comp = c.getComponent(i);
			if ( comp instanceof AbstractButton )
				return (AbstractButton)comp;
			else if ( comp instanceof Container )
			{
				AbstractButton result = getOkButton((Container)comp);
				if ( result != null)
				{
					String text = result.getText();
					if ( text != null && text.compareTo(mData[1].toString())==0)
						return result;
				}
			}
		}
		return null;
	}
	
	private JTextField m_filePathTextField;
	private AbstractButton m_okButton;

}
