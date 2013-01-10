package com.qspin.qtaste.tools.converter.ui.model.table;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.qspin.qtaste.tools.converter.model.EventManager;
import com.qspin.qtaste.tools.converter.model.event.Event;

public class AliasTableModel extends AbstractTableModel implements PropertyChangeListener {

	public AliasTableModel()
	{
		super();
		mEvents = new ArrayList<Event>();
		loadEvents();
		EventManager.getInstance().addPropertyChangeListener(this);
	}
	
	@Override
	public int getRowCount() {
		return mEvents.size();
	}

	@Override
	public int getColumnCount() {
		return _COLUMN_NAMES.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex)
		{
		case 0 : return mEvents.get(rowIndex).getComponentName();
		case 1 : return mEvents.get(rowIndex).getAlias();
		default : return null;
		}
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return columnIndex == 1;
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		if ( columnIndex == 1)
		{
			EventManager.getInstance().setComponentAlias(mEvents.get(rowIndex).getComponentName(), aValue.toString());
		}
	}
	
	@Override
	public String getColumnName(int columnIndex)
	{
		return _COLUMN_NAMES[columnIndex];
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ( evt.getPropertyName().equals(EventManager.DATA_CHANGE_PROPERTY_ID))
		{
			loadEvents();
			fireTableDataChanged();
		}
	}
	
	private void loadEvents()
	{
		mEvents.clear();
		for ( Object componentName : EventManager.getInstance().getComponentNames() )
		{
			mEvents.add(EventManager.getInstance().getEventsForComponent(componentName.toString()).get(0));
		}
	}
	
	private static final String[] _COLUMN_NAMES = new String[]{"Component name", "Alias"};
	private List<Event> mEvents;

}
