/*
    Copyright 2007-2009 QSpin - www.qspin.be

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

package com.qspin.qtaste.ui.xmleditor;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import com.qspin.qtaste.testsuite.TestRequirement;

@SuppressWarnings("serial")
public class TestRequirementTableModel extends DefaultTableModel {

   public TestRequirementTableModel() {
	  super();
	  setRequirements(null);
   }
      
    public Vector<?> getColumnIdentifiers() {
        return columnIdentifiers;
    }
    
    public void setRequirements(List<TestRequirement> pRequirements)
    {
    	if ( pRequirements == null )
    	{
    		mRequirements.clear();
    	} else {
    		mRequirements = pRequirements;
    	}
    	
    	addColumn(TestRequirement.ID);
    	addColumn(TestRequirement.DESCRIPTION);
    	for ( TestRequirement req : mRequirements )
    	{
    		for ( String dataId : req.getDataId() ) {
    			if ( findColumn(dataId) == -1 ) {
    				addColumn(dataId);
    			}
    		}
    	}
    	fireTableDataChanged();
    }
    
    @Override
    public int getRowCount()
    {
    	return mRequirements == null ? 0 : mRequirements.size();
    }
    
    @Override
    public Object getValueAt(int pRowIndex, int pColumnIndex)
    {
    	return mRequirements.get(pRowIndex).getData(getColumnName(pColumnIndex));
    }
    
    @Override
    public void setValueAt(Object pValue, int pRowIndex, int pColumnIndex)
    {
    	mRequirements.get(pRowIndex).setData(getColumnName(pColumnIndex), pValue.toString());
    }
    
	@Override
	public boolean isCellEditable(int row, int column) {
		return true;
	}

	public void addRequirement(TestRequirement pRequirementToAdd, int pIndex) {
		mRequirements.add(pIndex, pRequirementToAdd);
		fireTableDataChanged();
	}

	public void removeRequirement(int pIndex) {
		mRequirements.remove(pIndex);
		fireTableDataChanged();
	}
    
    public List<TestRequirement> getRequirements()
    {
    	return mRequirements;
    }
    
    private List<TestRequirement> mRequirements = new ArrayList<TestRequirement>();
}
