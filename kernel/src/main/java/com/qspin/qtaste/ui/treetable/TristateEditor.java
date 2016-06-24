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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.qspin.qtaste.ui.treetable;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import com.qspin.qtaste.ui.tools.TristateCheckBox;

public class TristateEditor extends AbstractCellEditor implements TableCellEditor {

    private static final long serialVersionUID = 1L;
    private TristateCheckBox check = null;
    private int currentRow, currentColumn;

    public TristateEditor() {
        //check = new DefaultCellEditor(new TristateCheckBox());
        check = new TristateCheckBox();
        check.setHorizontalAlignment(JLabel.CENTER);

        check.addActionListener(e -> stopCellEditing());

    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

        currentRow = row;
        currentColumn = column;
        if (value instanceof TristateCheckBox.State) {
            getCheck().setState((TristateCheckBox.State) value);
        }
        this.fireEditingStopped();

        return getCheck();
    }

    public Object getCellEditorValue() {
        return getCheck().getState();
    }

    public int getCurrentRow() {
        return currentRow;
    }

    public int getCurrentColumn() {
        return currentColumn;
    }

    public TristateCheckBox getCheck() {
        return check;
    }

}
    
    
