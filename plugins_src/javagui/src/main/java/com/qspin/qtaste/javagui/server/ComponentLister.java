package com.qspin.qtaste.javagui.server;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPopupMenu;

/**
 * Component asker responsible for the listing of all component's names.
 *  
 * @author simjan
 *
 */
final class ComponentLister extends ComponentCommander {

	/**
	 * Lists all component's names and counts the number of instances which have the same names.
	 * @return an array of Strings that have the following format : component_name (number of instance : X) 
	 */
	@Override
	String[] executeCommand(Object... data)
	{
		mComponentsMap = new HashMap<String, List<Component>>();
		
		for (Frame frame: Frame.getFrames()) {
			if (frame.getName() != null) {
				addToMap(frame);
			}
		}
		for (Window window: Window.getWindows()) {
			if (window.getName() != null) {
				addToMap(window);
			}
			browseComponent(window.getComponents());
		}
		
		ArrayList<String> list = new ArrayList<String>();
		for ( String key : mComponentsMap.keySet() )
		{
			list.add(key + "   (number of instance with this name :" + mComponentsMap.get(key).size() + ")");
		}
		Collections.sort(list);
		list.add("Number of ownerless windows : " + Window.getOwnerlessWindows().length);
		list.add("Number of windows : " + Window.getWindows().length);
		list.add("Number of frames : " + Frame.getFrames().length);
		String[] result = (String[]) list.toArray(new String[0]);
		return result;
	}
	
	private void addToMap(Component c)
	{
		String componentName = c.getName();
		if ( !mComponentsMap.containsKey(componentName) )
		{
			mComponentsMap.put(componentName, new ArrayList<Component>());
		}
		if ( !mComponentsMap.get(componentName).contains(c) )
		{
			mComponentsMap.get(componentName).add(c);
		}
	}

	private void browseComponent(Component[] components) {
		for (int c = 0; c < components.length; c++) {			
			String componentName = components[c].getName();
			// LOGGER.debug("browsing " + components[c].toString());
			// LOGGER.debug("name=" + componentName);
			if (componentName != null) {
				//LOGGER.debug("Component:" + componentName + " is found!");
				//if (!componentName.startsWith("null."))
				addToMap(components[c]);
			}
			if (components[c] instanceof Container) {
				if (components[c] instanceof JPopupMenu)
				{
					LOGGER.debug("detected JPopupMenu !!!!");
				}
				browseComponent(((Container) components[c]).getComponents());
			}
		}
	}
	
	private Map<String, List<Component>> mComponentsMap;
}
