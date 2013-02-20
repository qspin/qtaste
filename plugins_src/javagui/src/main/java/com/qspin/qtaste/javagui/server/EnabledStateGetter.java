package com.qspin.qtaste.javagui.server;

import java.awt.Component;

import com.qspin.qtaste.testsuite.QTasteTestFailException;

class EnabledStateGetter extends ComponentCommander {

	@Override
	Boolean executeCommand(Object... data) throws QTasteTestFailException {
		Component c = getComponentByName(data[0].toString());
		return c==null?false:c.isEnabled();
	}

}
