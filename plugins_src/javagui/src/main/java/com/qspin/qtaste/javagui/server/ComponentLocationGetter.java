package com.qspin.qtaste.javagui.server;

import java.awt.Component;
import java.awt.Point;

import com.qspin.qtaste.testsuite.QTasteException;

public class ComponentLocationGetter extends ComponentCommander {

	@Override
	Point executeCommand(Object... data) throws QTasteException {
		Component c = getComponentByName(data[0].toString());
		return c.getLocationOnScreen();
	}
}
