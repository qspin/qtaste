package com.qspin.qtaste.javagui.server;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;

import javax.swing.text.JTextComponent;

import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

public class ComponentColorGetter extends ComponentCommander {

	@Override
	String executeCommand(Object... data) throws QTasteException {
		Component c = getComponentByName(data[0].toString());
		if (c != null) {
			if (c instanceof JTextComponent) {
				return getHexadecimalColor(((JTextComponent) c).getForeground());
			} else if (c instanceof Container) {
				return getHexadecimalColor(((Container) c).getBackground());
			} else {
				throw new QTasteTestFailException("It is not possible to retrieve the color of this kind of component " + c.getClass() );
			}
		}
		return "";
	}
	
	String getHexadecimalColor(Color c)
	{
		String colorCode = "#";
		if ( c.getRed() < 16 )
			colorCode += "0";
		colorCode += Integer.toHexString(c.getRed());
		if ( c.getGreen() < 16 )
			colorCode += "0";
		colorCode += Integer.toHexString(c.getGreen());
		if ( c.getBlue() < 16 )
			colorCode += "0";
		colorCode += Integer.toHexString(c.getBlue());
		return colorCode;
	}

}
