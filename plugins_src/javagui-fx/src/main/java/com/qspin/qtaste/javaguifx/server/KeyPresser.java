
/*
    Copyright 2007-2012 QSpin - www.qspin.be

    This file is part of QTaste framework.

    QTaste is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    QTaste is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with QTaste. If not, see <http://www.gnu.org/licenses/>.
*/

package com.qspin.qtaste.javaguifx.server;

import java.awt.Robot;

import com.qspin.qtaste.testsuite.QTasteTestFailException;

class KeyPresser extends ComponentCommander {

	@Override
	public Object executeCommand(int timeout, String componentName, Object... data) throws QTasteTestFailException {
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
