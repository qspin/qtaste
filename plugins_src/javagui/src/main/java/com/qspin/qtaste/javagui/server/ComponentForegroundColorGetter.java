package com.qspin.qtaste.javagui.server;

import java.awt.Component;

import javax.swing.text.JTextComponent;

import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

public class ComponentForegroundColorGetter extends ComponentBackgroundColorGetter {

	@Override
	String executeCommand(Object... data) throws QTasteException {
		Component c = getComponentByName(data[0].toString());
		if (c != null) {
			if (c instanceof JTextComponent) {
				return getHexadecimalColor(((JTextComponent) c).getForeground());
			} else {
				throw new QTasteTestFailException("It is not possible to retrieve the foreground color of this kind of component " + c.getClass() );
			}
		}
		return "";
	}
}
