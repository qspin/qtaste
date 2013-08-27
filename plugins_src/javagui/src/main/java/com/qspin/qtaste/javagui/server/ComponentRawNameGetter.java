package com.qspin.qtaste.javagui.server;

import java.awt.Component;

import com.qspin.qtaste.testsuite.QTasteException;

public class ComponentRawNameGetter extends ComponentCommander {

	@Override
	String executeCommand(Object... data) throws QTasteException {
		Component c = getComponentByName(data[0].toString());
		if (c != null) {
			return c.getName();
		}
		return null;
	}

}
