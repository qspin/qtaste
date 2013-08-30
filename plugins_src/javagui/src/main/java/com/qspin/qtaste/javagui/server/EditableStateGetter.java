package com.qspin.qtaste.javagui.server;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.text.JTextComponent;

import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

/**
 * Component asker which return the editable state of a component.
 * @author simjan
 *
 */
class EditableStateGetter extends ComponentCommander {

	/**
	 * @param data the component's name.
	 * @return <code>true</code> if the component is editable.
	 * @throws QTasteTestFailException if no component is found.
	 */
	@Override
	Boolean executeCommand(Object... data) throws QTasteException {
		Component c = getComponentByName(data[0].toString());
		if ( c == null )
			return false;
		
		if( c instanceof JTextComponent )
			return ((JTextComponent) c).isEditable();
		
		else if( c instanceof JComboBox )
			return ((JComboBox) c).isEditable();
		
		else if( c instanceof JTable )
		{
			for (int x = 0; x < ((JTable)c).getColumnCount(); x++)
			{
				for (int y = 0; y < ((JTable)c).getRowCount(); y++)
				{
					if ( ((JTable)c).isCellEditable(y, x) )
					{
						return true;
					}
				}
			}
		}
		else if( c instanceof JTree )
			return ((JTree) c).isEditable();
		
		else 
			throw new QTasteTestFailException("Cannot get the editable state of a component of type " + c.getClass() + ".");
		
		return false;
	}

}
