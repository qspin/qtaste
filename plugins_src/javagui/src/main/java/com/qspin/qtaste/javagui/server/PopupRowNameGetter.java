package com.qspin.qtaste.javagui.server;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;

import com.qspin.qtaste.testsuite.QTasteException;

/**
 * Commander wich parses all Popups in order to find the contained message. 
 * @author simjan
 *
 */
public class PopupRowNameGetter extends ComponentCommander {

	/**
	 * Commander wich parses all Popups in order to find the contained message. 
	 * @param BOOLEAN value : <code>true</code> means that only the text of the active popup has to be returned.
	 * 						  <code>false</code> means that all texts have to be returned.
	
	 * @return the list containing the found texts.
	 * @throws QTasteException
	 */
	@Override
	List<String> executeCommand(Object... data) throws QTasteException {
		boolean onlyWithFocus = (Boolean)data[0];
		List<String> texts = new ArrayList<String>();
		for ( JDialog dialog : findPopups() )
		{
			if ( onlyWithFocus )
			{
				setComponentFrameVisible(dialog);
				// if only the main popup text is needed, ignored popup without focus
				if ( !hasTheFocus(dialog))
					{
					LOGGER.info("the dialog with the title '" + dialog.getTitle() + "' will be ignored");
					continue;
				}
			}
			
			LOGGER.info("the dialog with the title '" + dialog.getTitle() + "' will not be ignored");
			
			//find the popup Component
			texts.add(dialog.getName());
		}
		return texts;
	}
	
	

}
