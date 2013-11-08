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

import java.awt.Component;
import java.awt.Window;

import com.qspin.qtaste.testsuite.QTasteException;

public class ComponentVisibilityChecker extends ComponentCommander {

	@Override
	Boolean executeCommand(int timeout, String componentName, Object... data) throws QTasteException {
		Component c = getComponentByName(componentName);
		Component currentComponent = c;
		if ( c == null )
		{
			LOGGER.debug("checkComponentIsVisible on a null component");
			return false;
		}
		while (currentComponent != null )
		{
			boolean lastRun = currentComponent instanceof Window; //Dialog can have another window as parent.
			
			if ( !currentComponent.isVisible() )
			{
				if ( c == currentComponent )
				{
					LOGGER.debug("The component " + c.getName() + " is not visible.");
				}
				else
				{
					LOGGER.debug("The parent (" + currentComponent.getName() + ") of the component " + c.getName() + " is not visible.");
				}
				return false;
			}
			if ( lastRun )
			{
				break;
			}
			else
				currentComponent = currentComponent.getParent();
		}
		return true;
	}
}
