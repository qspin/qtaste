package com.qspin.qtaste.javagui.server;

import com.qspin.qtaste.testsuite.QTasteException;

/**
 * This component checks if there is at least one popup displayed. 
 * @author simjan
 *
 */
public class PopupChecker extends ComponentCommander {

	@Override
	Boolean executeCommand(Object... data) throws QTasteException {
		return !findPopups().isEmpty();
	}
}
