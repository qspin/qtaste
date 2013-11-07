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
import java.awt.Container;
import java.util.List;

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
			JDialog targetPopup = null;
			for (JDialog dialog : findPopups() )
			{
				if ( !dialog.isVisible() || !dialog.isEnabled() )
				{
					String msg = "Ignore the dialog '" + dialog.getTitle() + "' cause:\n ";
					if (!dialog.isVisible())
						msg += "\t is not visible";
					if (!dialog.isEnabled())
						msg += "\t is not enabled";
					LOGGER.info(msg);
					continue;
				}
				if (activateAndFocusComponentWindow(dialog))
				{
					targetPopup = dialog;
				}
				else
				{
					LOGGER.info("Ignore the dialog '" + dialog.getTitle() + "' cause:\n  \t is not focused");
				}
			}
			component = findButtonComponent(targetPopup, buttonText);
			
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
		synchronizeThreads();
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
