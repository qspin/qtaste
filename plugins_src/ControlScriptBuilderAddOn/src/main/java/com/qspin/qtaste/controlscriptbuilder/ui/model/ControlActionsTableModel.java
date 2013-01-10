package com.qspin.qtaste.controlscriptbuilder.ui.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.qspin.qtaste.controlscriptbuilder.model.ControlAction;


@SuppressWarnings("serial")
public class ControlActionsTableModel extends AbstractTableModel {

	public ControlActionsTableModel()
	{
		mControlActions = new ArrayList<ControlAction>();
	}
	
	@Override
	public int getRowCount() {
		return mControlActions.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}
	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
    }

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch(columnIndex)
		{
		case 0 : return rowIndex;
		case 1 : return mControlActions.get(rowIndex).getType().toString();
		case 2 : return mControlActions.get(rowIndex).toString();
		default : return null;
		}
	}
	
	public void setControlActions(List<ControlAction> actions) {
		if ( actions != null )
		{
			mControlActions = actions;
		} else {
			mControlActions.clear();
		}
		fireTableDataChanged();
	}
	
	public ControlAction getControlAction(int pRowIndex)
	{
		return mControlActions.get(pRowIndex);
	}

	public void remove(int selectedRow) {
		mControlActions.remove(selectedRow);
		fireTableDataChanged();
	}

	public void moveUp(int selectedRow) {
		ControlAction action = mControlActions.remove(selectedRow);
		mControlActions.add(selectedRow-1, action);
		fireTableDataChanged();
	}

	public void moveDown(int selectedRow) {
		ControlAction action = mControlActions.remove(selectedRow);
		mControlActions.add(selectedRow+1, action);
		fireTableDataChanged();
	}
	
	private List<ControlAction> mControlActions;

	private static final String[] COLUMN_NAMES = {"Index", "Type", "Name"};

}
