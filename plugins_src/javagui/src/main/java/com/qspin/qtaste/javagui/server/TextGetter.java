package com.qspin.qtaste.javagui.server;

import java.awt.Component;

import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.text.JTextComponent;

import com.qspin.qtaste.testsuite.QTasteTestFailException;

class TextGetter extends ComponentCommander {
	
	@Override
	public String executeCommand(Object... data) throws QTasteTestFailException {
		Component c = getComponentByName(data[0].toString());
		if (c != null) {
			if (c instanceof JLabel) {
				return ((JLabel) c).getText();
			} else if (c instanceof JTextComponent) {
				return ((JTextComponent) c).getText();
			} else if (c instanceof AbstractButton) {
				return ((AbstractButton) c).getText();
			}
		}
		return null;
	}

}
