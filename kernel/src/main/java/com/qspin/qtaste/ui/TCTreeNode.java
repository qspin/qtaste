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
*/
package com.qspin.qtaste.ui;

import javax.swing.tree.DefaultMutableTreeNode;

import com.qspin.qtaste.ui.tools.FileNode;
import com.qspin.qtaste.ui.tools.TestDataNode;

@SuppressWarnings("serial")
public class TCTreeNode extends DefaultMutableTreeNode{


    public TCTreeNode(Object userObject, boolean allowsChildren){
        super( userObject, allowsChildren );
    }

    protected boolean isTestcaseDir()
    {
        if (getUserObject() instanceof FileNode) {
            FileNode fn = (FileNode)getUserObject();
            return !fn.isTestcaseDir() && getParent() != null;
        }
        else return false;
        
    }
    
    public boolean isLeaf() {
        if (getUserObject() instanceof FileNode) {
            FileNode fn = (FileNode)getUserObject();
            return fn.isTestcaseDir() && getParent() != null && (fn.isTestcaseDir() && !fn.isShowTestData() || fn.getChildren()== null || fn.getChildren().length==0);
        }
        else if (getUserObject() instanceof TestDataNode) {
            return true;
        }
        else return false;
    }

}
