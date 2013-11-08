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

package com.qspin.qtaste.javagui.server;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;

import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

public class ComponentBackgroundColorGetter extends ComponentCommander {

	@Override
	String executeCommand(int timeout, String componentName, Object... data) throws QTasteException {
		Component c = getComponentByName(componentName);
		if (c != null) {
			if (c instanceof Container) {
				return getHexadecimalColor(((Container) c).getBackground());
			} else {
				throw new QTasteTestFailException("It is not possible to retrieve the background color of this kind of component " + c.getClass() );
			}
		}
		return "";
	}
	
	protected String getHexadecimalColor(Color c)
	{
		String colorCode = "#";
		if ( c.getRed() < 16 )
			colorCode += "0";
		colorCode += Integer.toHexString(c.getRed());
		if ( c.getGreen() < 16 )
			colorCode += "0";
		colorCode += Integer.toHexString(c.getGreen());
		if ( c.getBlue() < 16 )
			colorCode += "0";
		colorCode += Integer.toHexString(c.getBlue());
		return colorCode;
	}

}
