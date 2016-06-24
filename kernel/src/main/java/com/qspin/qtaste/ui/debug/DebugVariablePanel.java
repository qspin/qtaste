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

package com.qspin.qtaste.ui.debug;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

/**
 * @author vdubois
 */
@SuppressWarnings("serial")
public class DebugVariablePanel extends JPanel {

    //private JTable variableTable;
    private DefaultTableModel tableModel;
    //private ArrayList<DebugVariable> mDebugVariables;
    private DebugTreeTable mTree;
    private DebugVariableTreeTableModel mTableModel;

    /*
    public DebugVariablePanel() {
        super(new BorderLayout());
        genUI();
    }
    */
    public DebugVariablePanel() {
        super(new BorderLayout());
        genUI();
    }

    public void setDebugVariables(ArrayList<DebugVariable> debugVariables) {
        /*
        // do a copy
    	ArrayList<DebugVariable> copyDebugVariables = (ArrayList<DebugVariable>)debugVariables.clone();
        mDebugVariables = copyDebugVariables;
        */
        mTableModel.setDebugVariables(debugVariables);
        mTree.repaint();
        mTree.getTree().expandPath(mTree.getTree().getPathForRow(0));
    }

    private void genUI() {
        /*
            tableModel = new DefaultTableModel(new Object[]{"Variable", "Value"}, 0) {
                @Override
                public boolean isCellEditable(int rowIndex, int mColIndex) {
                    return false;
                }
            };        
            variableTable = new JTable(tableModel);
            add(new JScrollPane(variableTable));
            
    	*/
        mTableModel = new DebugVariableTreeTableModel();
        mTree = new DebugTreeTable(mTableModel);
        JScrollPane sp = new JScrollPane(mTree);
        this.add(sp);

        //mTree = new VariableTree(mDebugVariables);

    }

    public DebugTreeTable getTreeTable() {
        return mTree;
    }

    public void dumpPythonVar(String s) {
        // parse the string
        tableModel.setRowCount(0);
        String[] lines = s.split("\\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String[] data = line.split("' = '");
            if (data.length == 2) {
                tableModel.addRow(new Object[] {data[0].replace("'", ""), "'" + data[1]});
            }
        }
    }
}
