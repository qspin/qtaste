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

package com.qspin.qtaste.ui.tools;

import java.io.File;

/**
 *
 * @author vdubois
 */
public class TreeNodeImpl  implements JTreeNode{
    protected File f;
    protected String displayValue;
    protected boolean bShowingTestData=false;
    Object[] children =null; 

    public TreeNodeImpl(File f, String displayValue) {
        this.f = f;
        this.displayValue = displayValue;
    }

    public File getFile() {
        return f;
    }
    public void setFile(File file) {
        f = file;
    }

    public boolean isDir() {
        return f.isDirectory();
    }

    public String toString() {
        return displayValue;
    }

    public Object[] getChildren() {
       return new Object[] {};

    }

    public String getId() {
        return "";
    }

    public boolean isShowTestData() {
        return bShowingTestData;
    }

    public void setShowTestdata(boolean value) {
        bShowingTestData = value;
    }
    
}
