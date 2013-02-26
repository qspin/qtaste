package com.qspin.qtaste.javagui.server;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;

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
		if (!component.isVisible())
			throw new QTasteTestFailException("The component \"" + componentName + "\" is not visible!");
		
		try {
			SwingUtilities.invokeAndWait(this);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if ( mError != null )
		{
			throw new QTasteTestFailException(mError.getMessage(), mError);
		}
		return true;
	}

	private void setData(Object[] data)
	{
		this.mData = data;
	}
	
	protected Object[] mData;
	protected Exception mError;
	protected Component component;
}
