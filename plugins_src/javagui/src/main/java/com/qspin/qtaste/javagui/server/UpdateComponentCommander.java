package com.qspin.qtaste.javagui.server;

import java.awt.Component;

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
		if (! chekComponentIsVisible(component))
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
	
	protected boolean chekComponentIsVisible(Component c)
	{
		Component currentComponent = c;
		while (currentComponent != null)
		{
			if ( !currentComponent.isVisible() )
			{
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
	
	protected Object[] mData;
	protected Component component;
}
