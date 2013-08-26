package com.qspin.qtaste.javagui.server;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

/**
 * Commander which sets a value in the input field of a popup.
 * @see JOptionPane#showInputDialog(Object)
 */
public class PopupTextSetter extends UpdateComponentCommander {

	/**
	 * Commander which sets a value in the input field of a popup.
	 * @param INTEGER - the timeout value; OBJECT - with the value to insert. The toString method will be used on the object.
	 * @return true if the command is successfully performed.
	 * @throws QTasteException
	 */
	@Override
	public Boolean executeCommand(Object... data) throws QTasteException {
		setData(data);
		int timeout = Integer.parseInt(mData[0].toString());
		long maxTime = System.currentTimeMillis() + 1000 * timeout;
		
		while ( System.currentTimeMillis() < maxTime )
		{
			for (JDialog dialog : findPopups() )
			{
				if ( !dialog.isVisible() || !dialog.isEnabled() || !dialog.isActive() )
				{
					String msg = "Ignore the dialog '" + dialog.getTitle() + "' cause:\n ";
					if (!dialog.isVisible())
						msg += "\t is not visible";
					if (!dialog.isEnabled())
						msg += "\t is not enabled";
					if (!dialog.isActive())
						msg += "\t is not active";
					LOGGER.info(msg);
					continue;
				}
				else
				{
					component = findTextComponent(dialog);
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
			throw new QTasteTestFailException("The text field component is not found.");
		}
		if (!component.isEnabled()) {
			throw new QTasteTestFailException("The text field component is not enabled.");
		}
		if (! checkComponentIsVisible(component))
			throw new QTasteTestFailException("The text field component is not visible!");
		
		prepareActions();
		SwingUtilities.invokeLater(this);
		return true;
	}
	
	private JTextField findTextComponent(Component c)
	{
		if ( c instanceof JTextField )
			return (JTextField)c;
		else if ( c instanceof Container )
		{
			for (Component comp : ((Container)c).getComponents() )
			{
				JTextField jtf = findTextComponent(comp);
				if ( jtf != null )
					return jtf;
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
		((JTextField)component).setText(mData[1].toString());
	}

}
