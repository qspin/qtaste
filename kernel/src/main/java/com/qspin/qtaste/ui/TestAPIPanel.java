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

package com.qspin.qtaste.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTree;

/**
 *
 * @author vdubois
 */
@SuppressWarnings("serial")
public class TestAPIPanel extends JPanel{

    protected JTree apiTree;
    public TestAPIPanel() {
            super(new BorderLayout());
    }
    public void setTestCaseTree(JTree tree)
    {
        apiTree = tree;
    }
}
