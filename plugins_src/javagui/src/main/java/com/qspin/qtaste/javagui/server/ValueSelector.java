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

public class ValueSelector extends UpdateComponentCommander {

    @Override
    protected void prepareActions() throws QTasteException {
        String value = mData[0].toString();
        if (component instanceof AbstractButton) {
            new ComponentSelector().executeCommand(timeout, componentName, Boolean.parseBoolean(value));
            return;
        }
        if (component instanceof JComboBox) {
            JComboBox combo = (JComboBox) component;
            ListCellRenderer renderer = combo.getRenderer();
            for (int i = 0; i < combo.getItemCount(); i++) {
                String itemValue = getItemText(combo.getModel().getElementAt(i), renderer);
                LOGGER.trace("compare combo elmt (" + itemValue + ") with '" + value + "'");
                // Use a startsWith instead of equals() as toString() can return more than the value
                if (itemValue.equals(value)) {
                    mValueToSelect = i;
                }
            }
        } else if (component instanceof JList) {
            JList list = (JList) component;
            ListCellRenderer renderer = list.getCellRenderer();
            for (int i = 0; i < list.getModel().getSize(); i++) {
                String itemValue = getItemText(list.getModel().getElementAt(i), renderer);
                LOGGER.trace("compare list elmt (" + itemValue + ") with '" + value + "'");
                if (itemValue.equals(value)) {
                    mValueToSelect = i;
                }
            }
        } else if (component instanceof JSpinner) {
            mValueToSelect = Double.parseDouble(value);
        } else if (component instanceof JSlider) {
            mValueToSelect = Integer.parseInt(value);
        } else {
            throw new QTasteTestFailException(
                  "component '" + component.getName() + "' (" + component.getClass() + ") found but unused");
        }
        if (mValueToSelect == null) {
            throw new QTasteTestFailException("Value '" + value + "' is not found!");
        }
    }

    @Override
    protected void doActionsInSwingThread() {
        if (component instanceof JComboBox) {
            ((JComboBox) component).setSelectedIndex(mValueToSelect.intValue());
        } else if (component instanceof JList) {
            ((JList) component).setSelectedIndex(mValueToSelect.intValue());
        } else if (component instanceof JSpinner) {
            JSpinner spinner = (JSpinner) component;
            spinner.getModel().setValue(mValueToSelect.doubleValue());
        } else if (component instanceof JSlider) {
            JSlider slider = (JSlider) component;
            slider.getModel().setValue(mValueToSelect.intValue());
        }
    }

    protected String getItemText(Object item, ListCellRenderer renderer) {
        Component c = renderer.getListCellRendererComponent(new JList(), item, 0, false, false);
        if (c instanceof Label) {
            return ((Label) c).getText();
        }
        if (c instanceof JLabel) {
            return ((JLabel) c).getText();
        }
        return item.toString();
    }

    protected Number mValueToSelect;

}
