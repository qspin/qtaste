package com.qspin.qtaste.controlscriptbuilder.ui.model;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import com.qspin.qtaste.controlscriptbuilder.model.ControlAction;
import com.qspin.qtaste.controlscriptbuilder.ui.editor.Editor;


@SuppressWarnings("serial")
public class ControlActionTableModel extends AbstractTableModel {

	public ControlActionTableModel()
	{
		mParameterNames = new ArrayList<String>();
		mParameterValues = new ArrayList<String>();
	}
	
	@Override
	public int getRowCount() {
		return Math.min(mParameterNames.size(), mParameterValues.size());
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
		case 0 : return mParameterNames.get(rowIndex);
		case 1 : return mParameterValues.get(rowIndex);
		default : return null;
		}
	}
	
	public void loadControlAction(ControlAction action) {
		mParameterNames.clear();
		mParameterValues.clear();
		Properties actionParameters = action.getParameters();
		Enumeration<Object> keys = actionParameters.keys();
		while ( keys.hasMoreElements() )
		{
			String key = keys.nextElement().toString();
			mParameterNames.add(key);
			mParameterValues.add(actionParameters.getProperty(key));
		}
		LOGGER.debug("Action " + action.toString() + " loaded (" + mParameterNames.size() + " parameter(s))");
		fireTableDataChanged();
	}

	private List<String> mParameterNames;
	private List<String> mParameterValues;

	private static final String[] COLUMN_NAMES = {"Parameter's name", "Value"};
	private static final Logger LOGGER = Logger.getLogger(Editor.class);

}
