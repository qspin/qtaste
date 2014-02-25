/*
    Copyright QSpin - www.qspin.be

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
import java.awt.Label;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import java.util.List;
import java.util.ArrayList;
import javax.swing.ListCellRenderer;
import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;


/**
 * ListDumper is responsible to return the content of a List.
 *  
 */
final class ListDumper extends ComponentCommander {

	@Override
	String [] executeCommand(int timeout, String componentName, Object... data) throws QTasteException {
		Component c = getComponentByName(componentName);
		List<String> foundItems = new ArrayList<String>();		
		if ( c instanceof JList )
		{
			JList list = (JList) c;
			ListCellRenderer renderer = list.getCellRenderer();			
			for (int i = 0; i < list.getModel().getSize(); i++) {
			        foundItems.add(getItemText(list.getModel().getElementAt(i), renderer));
			}
			return foundItems.toArray(new String []{});
		} else if (c instanceof JComboBox){
			JComboBox combo = (JComboBox) c;
			ListCellRenderer renderer = combo.getRenderer();
			for (int i = 0; i < combo.getItemCount(); i++) {
				foundItems.add(getItemText(combo.getModel().getElementAt(i), renderer));
			}
			return foundItems.toArray(new String []{});
		} else {
			throw new QTasteTestFailException("The component \"" + componentName + "\" is not a JList or JComboBox");
		}
	}
	protected String getItemText(Object item, ListCellRenderer renderer)
	{
		Component c = renderer.getListCellRendererComponent(new JList(), item, 0, false, false);
		if ( c instanceof Label )
		{
			return ((Label)c).getText();
		} 
		else if ( c instanceof JLabel )
		{
			return ((JLabel)c).getText();
		}
		else
		{
			LOGGER.warn("Unknown label type : " + c.getClass());
			return item.toString();
		}
	}

}
