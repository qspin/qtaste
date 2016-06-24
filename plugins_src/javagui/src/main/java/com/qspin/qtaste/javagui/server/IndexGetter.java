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
import javax.swing.JList;

import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

public class IndexGetter extends ComponentCommander {

    @Override
    Integer executeCommand(int timeout, String componentName, Object... data) throws QTasteException {
        Component component = getComponentByName(componentName);

        if (component instanceof JComboBox) {
            JComboBox<?> combo = (JComboBox<?>) component;
            return combo.getSelectedIndex();
        } else if (component instanceof JList) {
            JList<?> list = (JList<?>) component;
            return list.getSelectedIndex();
        }

        throw new QTasteTestFailException(
              "The component \"" + componentName + "\" is not a supported component (" + component.getClass().getName() + ")");
    }
}
