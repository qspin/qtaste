/*
    Copyright 2007-2012 QSpin - www.qspin.be

    This file is part of QTaste framework.

    QTaste is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    QTaste is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with QTaste. If not, see <http://www.gnu.org/licenses/>.
*/

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
		String value = mData[0].toString();
										
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
