package com.qspin.qtaste.javagui.server;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Window;

import org.apache.log4j.Logger;

import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

abstract class ComponentCommander {

	abstract Object executeCommand(Object... data) throws QTasteException;
	
	protected static final Logger LOGGER = Logger.getLogger(ComponentCommander.class);
	
	protected Component getComponentByName(String name) throws QTasteTestFailException {
		// TODO: Think about several component having the same names!
		Component foundComponent = null;
		for (int w = 0; w < Frame.getWindows().length; w++) {
			Window window = Frame.getWindows()[w];
			if (window.getName().equals(name)) {
				return window;
			}
			Component c = lookForComponent(name, window.getComponents());
			if (c != null) {
				c.requestFocus();
				foundComponent = c;				
			}
		}
		if ( foundComponent != null )
		{
			return foundComponent;
		}
		throw new QTasteTestFailException("The component \"" + name + "\" is not found.");
	}

	private Component lookForComponent(String name, Component[] components) {
		for (int i = 0; i < components.length; i++) {
			//String componentName = ComponentNamer.getInstance().getNameForComponent(components[c]);
			Component c = components[i];
			if (c != null && c.getName() != null && c.getName().contains(name)) {
				System.out.println("Component:" + name + " is found!");
				return c;
			} else {
				if (c instanceof Container) {
					Component result = lookForComponent(name,
							((Container) c).getComponents());
					if (result != null) {
						return result;
					}
				}
			}
		}
		return null;
	}

}
