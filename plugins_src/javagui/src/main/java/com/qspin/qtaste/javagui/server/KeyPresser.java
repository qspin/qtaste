package com.qspin.qtaste.javagui.server;

import java.awt.Robot;

import com.qspin.qtaste.testsuite.QTasteTestFailException;

class KeyPresser extends ComponentCommander {

	@Override
	public Object executeCommand(Object... data) throws QTasteTestFailException {
		Robot bot = (Robot)data[0];
		int keycode = Integer.parseInt(data[1].toString());
		long delay = Long.parseLong(data[2].toString());
		if (bot == null)
			throw new QTasteTestFailException("JavaGUI cannot pressKey if java.awt.Robot is not available!");
		bot.keyPress(keycode);
		try {			
			Thread.sleep(delay);
		}
		catch (InterruptedException e) { 
			e.printStackTrace();
		}	
		bot.keyRelease(keycode);
		return null;
	}
	
}
