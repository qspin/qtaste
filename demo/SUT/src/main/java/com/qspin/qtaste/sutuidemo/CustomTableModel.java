package com.qspin.qtaste.sutuidemo;

import java.util.List;

import javax.swing.table.AbstractTableModel;

final class CustomTableModel extends AbstractTableModel {

	public CustomTableModel(List<Person> pPeople)
	{
		super();
		mData = pPeople;
	}
	
	public int getRowCount() {
		return mData.size();
	}

	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		Person p = mData.get(rowIndex);
		switch(columnIndex)
		{
		case 0 : return p.getFirstName();
		case 1 : return p.getLastName();
		case 2 : return p.getAge();
		case 3 : return p.getAdress();
		}
		return null;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch(columnIndex)
		{
		case 2 : return Integer.class;
		default : return String.class;
		}
	}

	@Override
	public String getColumnName(int columnIndex) {
		return COLUMN_NAMES[columnIndex];
	}

	private static final String[] COLUMN_NAMES = {"First name", "Last name", "Age", "Adress"}; 
	private List<Person> mData;
}
