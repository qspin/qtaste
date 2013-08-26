package com.qspin.qtaste.javagui.server;

import java.awt.Component;

import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

/**
 * Component asker which return the enabled stated of a component.
 * @author simjan
 *
 */
class EnabledStateGetter extends ComponentCommander {

	/**
	 * @param data the component's name.
	 * @return <code>true</code> if the component is enabled.
	 * @throws QTasteTestFailException if no component is found.
	 */
	@Override
	Boolean executeCommand(Object... data) throws QTasteException {
		Component c = getComponentByName(data[0].toString());
		return c==null?false:c.isEnabled();
	}

}
