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
