/*
    Copyright 2007-2015 QSpin - www.qspin.be

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

package com.qspin.qtaste.javaguifx.server;

import javafx.scene.Node;

import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

/**
 * Get information about the selected tab in a JTabbedPane (index, title or component name).
 */
public class TabGetter extends ComponentCommander {

    /**
     * Information to get from the selected tab
     */
    public enum InfoSelector {
        GET_INDEX,
        GET_TITLE,
        GET_COMPONENT_ID
    }

    /**
     * Constructor
     *
     * @param infoSelector information to get from the selected tab
     */
    public TabGetter(InfoSelector infoSelector) {
        mInfoSelector = infoSelector;
    }

    /**
     * Executes the a command on a component.
     *
     * @param timeout a timeout for the command execution (not used here)
     * @param componentName name of the component to execute the command on.
     * @param data additional data (not used here)
     * @return the selected tab index, title or id, according to the specified info selector.
     * @throws QTasteException
     */
    @Override
    public String executeCommand(int timeout, String componentName, Object... data) throws QTasteException {

        Node component = getComponentByName(componentName);

        // sanity checks
        if (component == null) {
            throw new QTasteTestFailException("Unable to find the component named '" + componentName + "'");
        }

        //		if (!(component instanceof JTabbedPane)) {
        //			throw new QTasteTestFailException("The component named '" + componentName + "' is not a JTabbedPane");
        //		}

        // get the requested value
        //		JTabbedPane tabbedPane   = (JTabbedPane)component;
        //		int 		currentIndex = tabbedPane.getSelectedIndex();
        String result = null;
        //
        //		switch (mInfoSelector) {
        //		case GET_INDEX:
        //			result = String.valueOf(currentIndex);
        //			break;
        //
        //		case GET_TITLE:
        //			if (currentIndex >= 0) {
        //				result = tabbedPane.getTitleAt(currentIndex);
        //			}
        //			break;
        //
        //		case GET_COMPONENT_ID:
        //			if (currentIndex >= 0) {
        //				result = tabbedPane.getComponentAt(currentIndex).getName();
        //			}
        //			break;
        //
        //		default:
        //            throw new QTasteTestFailException("Bad selector identifier");
        //		}

        return result;
    }

    private InfoSelector mInfoSelector;
}