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

import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

import javax.swing.*;
import java.awt.*;

public class ValueGetter extends ComponentCommander {

	@Override
	String executeCommand(int timeout, String componentName, Object... data) throws QTasteException {
		Component c = getComponentByName(componentName);
        if (c instanceof JComboBox) {
            JComboBox combo = (JComboBox) c;
            ListCellRenderer renderer = combo.getRenderer();
            return getItemText(combo.getModel().getSelectedItem(), renderer);
        } else if (c instanceof JList) {
			JList list = (JList) c;
			ListCellRenderer renderer = list.getCellRenderer();
            return getItemText(list.getSelectedValue(), renderer);
		} else if (c instanceof JSpinner) {
            JSpinner spinner = (JSpinner)c;
            return ((JSpinner) c).getModel().getValue().toString();
        } else if (c instanceof JSlider) {
            JSlider slider = (JSlider) c;
            return Integer.toString(slider.getModel().getValue());
        }
		throw new QTasteTestFailException("The component \"" + componentName + "\" is not a supported");
	}

    protected String getItemText(Object item, ListCellRenderer renderer)
    {
        Component c = renderer.getListCellRendererComponent(new JList(), item, 0, false, false);
        if ( c instanceof Label )
        {
            return ((Label)c).getText();
        }
        if ( c instanceof JLabel )
        {
            return ((JLabel)c).getText();
        }
        return item.toString();
    }
}
