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

package com.qspin.qtaste.ui.csveditor;

import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

// This class adds a method to retrieve the columnIdentifiers
// which is needed to implement the removal of
// column data from the table model
@SuppressWarnings("serial")
public class TestDataTableModel extends DefaultTableModel {

    /**
     * Stores modified state of document
     */
    //private boolean modified;

    /**
     * Stores file name of current document
     */
    //private JTable table;
    public TestDataTableModel(JTable table) {
        super();
        //modified = false;
        //this.table = table;
    }

    public void setModified(boolean modified) {
        //this.modified = modified;
    }

    public Vector<?> getColumnIdentifiers() {
        return columnIdentifiers;
    }
}
