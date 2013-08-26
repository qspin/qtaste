package com.qspin.qtaste.javagui.server;

import java.awt.Component;
import java.awt.Container;

import javax.swing.AbstractButton;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

/**
 * Commander which clicks on a popup button.
 */
public class PopupButtonClicker extends UpdateComponentCommander {

	/**
	 * Commander which clicks on a popup button.
	 * @param INTEGER - the timeout value; String - the button text.
	 * @return true if the command is successfully performed.
	 * @throws QTasteException
	 */
	@Override
	public Boolean executeCommand(Object... data) throws QTasteException {
		setData(data);
		int timeout = Integer.parseInt(mData[0].toString());
		long maxTime = System.currentTimeMillis() + 1000 * timeout;
		String buttonText = mData[1].toString();
		component = null;
		
		while ( System.currentTimeMillis() < maxTime )
		{
			for (JDialog dialog : findPopups() )
			{
				//ignored popup without focus
				if ( !dialog.isVisible() || !dialog.isEnabled() || !dialog.isActive() )
					continue;
				else
				{
					component = findButtonComponent(dialog, buttonText);
					if ( component != null && component.isEnabled() && checkComponentIsVisible(component) )
						break;
				}
			}
			if ( component != null && component.isEnabled() && checkComponentIsVisible(component) )
				break;
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				LOGGER.warn("Exception during the component search sleep...");
			}
		}
		
		if (component == null )
		{
			throw new QTasteTestFailException("The button with the text \"" + buttonText + "\" is not found.");
		}
		if (!component.isEnabled()) {
			throw new QTasteTestFailException("The button with the text \"" + buttonText + "\" is not enabled.");
		}
		if (! checkComponentIsVisible(component) )
			throw new QTasteTestFailException("The button with the text \"" + buttonText + "\" is not visible!");
		
		prepareActions();
		SwingUtilities.invokeLater(this);
		return true;
	}
	
	private AbstractButton findButtonComponent(Component c, String buttonText)
	{
		if ( (c instanceof AbstractButton) && (((AbstractButton)c).getText().equals(buttonText)) )
			return (AbstractButton)c;
		else if ( c instanceof Container )
		{
			for (Component comp : ((Container)c).getComponents() )
			{
				AbstractButton ab = findButtonComponent(comp, buttonText);
				if ( ab != null )
					return ab;
			}
		}
		return null;
	}
	
	@Override
	protected void prepareActions() throws QTasteTestFailException {
		//Do nothing
	}

	@Override
	protected void doActionsInSwingThread() throws QTasteTestFailException {
		((AbstractButton)component).doClick();
	}

}
