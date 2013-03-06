package com.qspin.qtaste.javagui.server;

import java.awt.Component;
import java.awt.Label;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import com.qspin.qtaste.testsuite.QTasteTestFailException;

final class TableRowSelector extends UpdateComponentCommander {

	@Override
	protected void prepareActions() throws QTasteTestFailException {
		if ( !(component instanceof JTable) )
		{
			throw new QTasteTestFailException("The component is not a JTable.");
		}
		mColumnName = mData[1].toString();
		mColumnValue = mData[2].toString();
		mOccurence = Integer.parseInt(mData[3].toString());
		int columnIndex = getColumnIndex(mColumnName, ((JTable)component).getModel());
		mRowToSelect = findRow(mColumnValue, columnIndex, (JTable)component);
	}

	@Override
	protected void doActionsInSwingThread() throws QTasteTestFailException {
		((JTable)component).getSelectionModel().setSelectionInterval(mRowToSelect, mRowToSelect);
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
	
	private int findRow(String pValue, int pColumnIndex, JTable pTable) throws QTasteTestFailException {
		int occurenceCounter = 0;
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
			{
				if ( occurenceCounter == mOccurence )
					return rowIndex;
			
				occurenceCounter ++;
			}
		}
		throw new QTasteTestFailException("Unable to find the occurence " + mOccurence + " of the value '" + mColumnValue + "' for the column '" + mColumnName + "'");
	}

	private String mColumnName;
	private String mColumnValue;
	private int mOccurence;
	private int mRowToSelect;
}
