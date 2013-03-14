package com.qspin.qtaste.javagui.server;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import com.qspin.qtaste.testsuite.QTasteTestFailException;

abstract class UpdateComponentCommander extends ComponentCommander implements Runnable {

	@Override
	public Boolean executeCommand(Object... data) throws QTasteTestFailException {
		setData(data);
		String componentName = mData[0].toString();
		component = getComponentByName(componentName);
		if (component == null )
		{
			throw new QTasteTestFailException("The component \"" + componentName + "\" is not found.");
		}
		if (!component.isEnabled()) {
			throw new QTasteTestFailException("The component \"" + componentName + "\" is not enabled.");
		}
		if (! checkComponentIsVisible(component))
			throw new QTasteTestFailException("The component \"" + componentName + "\" is not visible!");
		prepareActions();
		SwingUtilities.invokeLater(this);
		return true;
	}
	
	public void run()
	{
		try {
			doActionsInSwingThread();
		}
		catch (QTasteTestFailException e) {
			LOGGER.fatal(e.getMessage(), e);
		}
	}
	
	protected Component getComponentByName(String name) throws QTasteTestFailException {
		mFoundComponents = new ArrayList<Component>();
		mFoundComponent = null;
		mFindWithEqual = false;
		LOGGER.debug("try to find a component with the name : " + name);
		// TODO: Think about several component having the same names!
		//search for all components which contains the name
		for (int w = 0; w < Frame.getWindows().length; w++) {
			Window window = Frame.getWindows()[w];
			LOGGER.debug("parse window");
			if ( checkName(name, window) )
			{
				mFoundComponents.add(window);
			}
			lookForComponent(name, window.getComponents());
		}
		LOGGER.debug( mFoundComponents.size() + " component(s) found with the contains");
		
		//if equals the remove others
		if ( mFindWithEqual )
		{
			for (int i=0; i<mFoundComponents.size(); )
			{
				if (!mFoundComponents.get(i).getName().equals(name))
				{
					mFoundComponents.remove(i);
				} else {
					i++;
				}
			}
			LOGGER.debug( mFoundComponents.size() + " component(s) found with the equals");
		}
		
		//Remove invisible components
		for (int i=0; i<mFoundComponents.size(); )
		{
			if (!checkComponentIsVisible(mFoundComponents.get(i)) )
			{
				mFoundComponents.remove(i);
			} else {
				i++;
			}
		}
		LOGGER.debug( mFoundComponents.size() + " visible component(s) found");
		
		if ( !mFoundComponents.isEmpty() )
		{
			mFoundComponent = mFoundComponents.get(0);
			mFoundComponent.requestFocus();
			return mFoundComponent;
		}
		throw new QTasteTestFailException("The component \"" + name + "\" is not found.");
	}

	protected Component lookForComponent(String name, Component[] components) {
		for (int i = 0; i < components.length; i++) {
			//String componentName = ComponentNamer.getInstance().getNameForComponent(components[c]);
			Component c = components[i];
			if ( checkName(name, c) )
			{
				LOGGER.debug("Component " + c.getName() + " added to the list of found components");
				mFoundComponents.add(c);
			}
			if (c instanceof Container) {
				LOGGER.trace("Will parse the container " + c.getName() );
				lookForComponent(name, ((Container) c).getComponents());
			}
		}
		return null;
	}
	
	protected boolean checkComponentIsVisible(Component c)
	{
		Component currentComponent = c;
		if ( c == null )
		{
			LOGGER.debug("checkComponentIsVisible on a null component");
			return false;
		}
		while (currentComponent != null)
		{
			if ( !currentComponent.isVisible() )
			{
				if ( c != currentComponent )
				{
					LOGGER.debug("The component " + c.getName() + " is not visible.");
				}
				else
				{
					LOGGER.debug("The parent (" + currentComponent.getName() + ") of the component " + c.getName() + " is not visible.");
				}
				return false;
			}
			currentComponent = currentComponent.getParent();
		}
		return true;
	}
	
	protected abstract void prepareActions() throws QTasteTestFailException;
	protected abstract void doActionsInSwingThread()throws QTasteTestFailException;

	private void setData(Object[] data)
	{
		this.mData = data;
	}
	
	private List<Component> mFoundComponents;
	protected Object[] mData;
	protected Component component;
}
