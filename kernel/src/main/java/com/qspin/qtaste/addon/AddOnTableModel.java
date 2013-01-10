package com.qspin.qtaste.addon;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.qspin.qtaste.util.Environment;


public class AddOnTableModel extends AbstractTableModel {

	public AddOnTableModel(List<AddOnMetadata> pAddons)
	{
		mAddons = pAddons;
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
	public int getRowCount() {
		return mAddons.size();
	}
	
	public Class<?> getColumnClass(int columnIndex) {
        switch(columnIndex)
        {
        	case 0 : return Boolean.class;
        	default : return String.class;
        }
    }

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch(columnIndex)
		{
		case 0 : return mAddons.get(rowIndex).getStatus() == AddOnMetadata.LOAD;
		case 1 : return mAddons.get(rowIndex).getName();
		case 2 : return mAddons.get(rowIndex).getVersion();
		case 3 : return mAddons.get(rowIndex).getJarName();
		case 4 : return mAddons.get(rowIndex).getStatus();
		default: return null;
		}
	}

    public boolean isCellEditable(int rowIndex, int columnIndex) {
    	return columnIndex == 0;
    }
	
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (columnIndex == 0)
		{
			if (Boolean.parseBoolean(aValue.toString()))
			{
				getAddonManager().loadAddOn(mAddons.get(rowIndex));
			}
			else
			{
				getAddonManager().unloadAddOn(mAddons.get(rowIndex));	
			}
		}
		fireTableDataChanged();
	}
	
    public AddOnMetadata getAddOnMetaData(int pRowIndex)
    {
    	return mAddons.get(pRowIndex);
    }
    
	protected AddOnManager getAddonManager()
	{
		return Environment.getEnvironment().getAddOnManager();
	}
	
	protected List<AddOnMetadata> mAddons;
	private static String[] COLUMN_NAMES = new String[]{"Active", "Name", "Version", "Jar name", "Status"};

}
