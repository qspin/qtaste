package com.qspin.qtaste.javagui.server;

import java.awt.Component;
import java.awt.Container;
import java.awt.TextComponent;
import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.text.JTextComponent;

import com.qspin.qtaste.testsuite.QTasteTestFailException;

class TextSetter extends UpdateComponentCommander {

	@Override
	protected void doActionsInSwingThread() throws QTasteTestFailException{
		String value = mData[2].toString();
										
		// Support for AWT
		if (component instanceof TextComponent) {					
			TextComponent t = (TextComponent) component;
			t.setText(value);
			forceToLooseFocus(component);						
		}
		
		// Support for Swing
	    if ( component instanceof JFormattedTextField )
	    {
			try
			{
				JFormattedTextField field = ((JFormattedTextField)component);
				field.requestFocus();
				field.setText(value);
				//launch an exception for invalid input
				field.commitEdit();
				//lose focus to format the value
				forceToLooseFocus(component);
			}
			catch (ParseException e)
			{
				// Invalid value in field
				//return false;
				//TODO: Handle the case of invalid values
			}
		}
		if (component instanceof JTextComponent) {
			JTextComponent t = (JTextComponent) component;
			t.setText(value);
			forceToLooseFocus(component);
		}
		//throw new QTasteTestFailException("JavaGUI cannot setText for such component " + c.getClass().getName());
	}
	
	private void forceToLooseFocus(Component c) {
		Container parent= c.getParent();
		while ( parent != null && !parent.isFocusable() )
		{
			parent.getParent();
		}
		if ( parent != null ) {
			parent.requestFocus();
		}
	}

	@Override
	protected void prepareActions() throws QTasteTestFailException {}

}
