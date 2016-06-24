/*
    Copyright 2007-2012 QSpin - www.qspin.be

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

package com.qspin.qtaste.javagui.server;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.text.JTextComponent;

import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

/**
 * Component asker which return the editable state of a component.
 *
 * @author simjan
 */
class EditableStateGetter extends ComponentCommander {

    /**
     * @param data the component's name.
     * @return <code>true</code> if the component is editable.
     * @throws QTasteTestFailException if no component is found.
     */
    @Override
    Boolean executeCommand(int timeout, String componentName, Object... data) throws QTasteException {
        Component c = getComponentByName(componentName);
        if (c == null) {
            return false;
        }

        if (c instanceof JTextComponent) {
            return ((JTextComponent) c).isEditable();
        } else if (c instanceof JComboBox) {
            return ((JComboBox) c).isEditable();
        } else if (c instanceof JTable) {
            for (int x = 0; x < ((JTable) c).getColumnCount(); x++) {
                for (int y = 0; y < ((JTable) c).getRowCount(); y++) {
                    if (((JTable) c).isCellEditable(y, x)) {
                        return true;
                    }
                }
            }
        } else if (c instanceof JTree) {
            return ((JTree) c).isEditable();
        } else {
            throw new QTasteTestFailException("Cannot get the editable state of a component of type " + c.getClass() + ".");
        }

        return false;
    }

}
