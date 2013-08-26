package com.qspin.qtaste.javagui.server;

import com.qspin.qtaste.testsuite.QTasteException;
/**
 * Component asker used to check if a component with a specific name exists.
 * 
 * @author simjan
 *
 */
class ExistenceChecker extends ComponentCommander {

	/**
	 * Checks if a component with the name exists.
	 * @param data the component's name.
	 * @return <code>true</code> if a component with this name exists.
	 */
	@Override
	Boolean executeCommand(Object... data) {
		try{
			return getComponentByName(data[0].toString()) != null; 
		} catch (QTasteException pExc)
		{
			return false;
		}
	}

}
