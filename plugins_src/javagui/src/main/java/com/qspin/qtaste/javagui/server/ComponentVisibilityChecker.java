package com.qspin.qtaste.javagui.server;

import java.awt.Component;
import java.awt.Window;

import com.qspin.qtaste.testsuite.QTasteException;

public class ComponentVisibilityChecker extends ComponentCommander {

	@Override
	Boolean executeCommand(Object... data) throws QTasteException {
		Component c = getComponentByName(data[0].toString());
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
