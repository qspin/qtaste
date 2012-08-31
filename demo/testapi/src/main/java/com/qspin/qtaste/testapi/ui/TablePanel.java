package com.qspin.qtaste.testapi.ui;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

final class TablePanel extends JPanel {

	public TablePanel()
	{
		super();
		setName(COMPONENT_NAME);
		
		genUI();
	}
	
	
	private void genUI()
	{
		prepareComponent();
		setLayout(new GridLayout(NUMBER_OF_COMPONENT,1, 5, 5));
		add(mTable);
	}
	
	private void prepareComponent()
	{
		mTable.setName("TABLE");
	}

	private JTable mTable = new JTable(new MyTableModel());
	
	private static final int NUMBER_OF_COMPONENT = 1;
	public static final String COMPONENT_NAME = "TABLE_PANEL";
	
	private class MyTableModel extends AbstractTableModel {

		public MyTableModel(){
			mData.add("listItem_01");
			mData.add("listItem_02");
			mData.add("listItem_03");
			mData.add("listItem_04");
			mData.add("listItem_05");
		}
		@Override
		public int getRowCount() {
			return mData.size();
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return columnIndex==0 ? rowIndex : mData.get(rowIndex);
		}

		private List<String> mData = new ArrayList<String>();
	}
}
