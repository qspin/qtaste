package com.qspin.qtaste.javagui.server;

import com.qspin.qtaste.testsuite.QTasteTestFailException;

class ExistenceChecker extends ComponentCommander {

	@Override
	Boolean executeCommand(Object... data) {
		try{
			return getComponentByName(data[0].toString()) != null; 
		} catch (QTasteTestFailException pExc)
		{
			return false;
		}
	}

}
