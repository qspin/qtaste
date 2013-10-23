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
import java.awt.Label;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

final class TableRowCounter extends ComponentCommander {

	@Override
	Integer executeCommand(Object... data) throws QTasteException {
		String componentName = data[1].toString();
		Component c = getComponentByName(componentName);
		if ( c == null || !(c instanceof JTable) )
		{
			throw new QTasteTestFailException("The component " + componentName + " is not a JTable.");
		}
		JTable table = (JTable)c;
		TableModel model = table.getModel();
		int columnIndex = getColumnIndex(data[2].toString(), model);
		return countRows(data[3].toString(), columnIndex, table);
	}
	
	private int countRows(String pValue, int pColumnIndex, JTable pTable) {
		int counter = 0;
		for (int rowIndex = 0; rowIndex < pTable.getModel().getRowCount(); ++rowIndex )
		{
			//format model value
			Object cellValue = pTable.getModel().getValueAt(rowIndex, pColumnIndex);
			TableCellRenderer renderer = pTable.getCellRenderer(rowIndex, pColumnIndex);
			Component c = renderer.getTableCellRendererComponent(pTable, cellValue, false, false, rowIndex, pColumnIndex);
			String valueRepresentation;
			if ( c instanceof Label )
			{
				LOGGER.debug("cell is represented by a Label");
				valueRepresentation = ((Label)c).getText();
			}
			else if ( c instanceof JLabel )
			{
				LOGGER.debug("cell is represented by a JLabel");
				valueRepresentation = ((JLabel)c).getText();
			}
			else
			{
				LOGGER.debug("cell is represented by a " + c.getClass());
				valueRepresentation = c.toString();
			}

			//check with searched value
			LOGGER.debug("compare value (" + pValue + ") with the cell value (" + valueRepresentation + ")");
			if (pValue.equals(valueRepresentation))
				counter ++;
		}
		return counter;
	}

	private int getColumnIndex(String pColumnName, TableModel pModel) throws QTasteTestFailException
	{
		for ( int columnIndex = 0; columnIndex < pModel.getColumnCount(); columnIndex++ )
		{
			if ( pColumnName.equals(pModel.getColumnName(columnIndex)) )
			{
				return columnIndex;
			}
		}
		throw new QTasteTestFailException("The column " + pColumnName + " is not found.");
	}

}
