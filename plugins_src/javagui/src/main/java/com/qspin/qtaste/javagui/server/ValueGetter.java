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
import java.awt.Label;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.ListCellRenderer;

import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

public class ValueGetter extends ComponentCommander {

    @Override
    Object executeCommand(int timeout, String componentName, Object... data) throws QTasteException {
        Component component = getComponentByName(componentName);

        if (component instanceof JComboBox) {
            JComboBox<?> combo = (JComboBox<?>) component;
            ListCellRenderer<?> renderer = combo.getRenderer();
            return getItemText(combo.getModel().getSelectedItem(), renderer);
        } else if (component instanceof JList) {
            JList<?> list = (JList<?>) component;
            ListCellRenderer<?> renderer = list.getCellRenderer();
            return getItemText(list.getSelectedValue(), renderer);
        } else if (component instanceof JSpinner) {
            return ((JSpinner) component).getModel().getValue().toString();
        } else if (component instanceof JSlider) {
            return Integer.toString(((JSlider) component).getModel().getValue());
        } else if (component instanceof AbstractButton) {
            return Boolean.toString(((AbstractButton) component).isSelected());
        }

        throw new QTasteTestFailException(
              "The component \"" + componentName + "\" is not a supported component (" + component.getClass().getName() + ")");
    }

    protected String getItemText(Object item, ListCellRenderer renderer) {
        Component component = renderer.getListCellRendererComponent(new JList(), item, 0, false, false);

        if (component instanceof Label) {
            return ((Label) component).getText();
        }
        if (component instanceof JLabel) {
            return ((JLabel) component).getText();
        }

        return item.toString();
    }
}
