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
import java.awt.Container;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

/**
 * Component asker responsible to count the number of GUI component that are enabled/disabled.
 *
 * @author simjan
 */
class EnabledComponentCounter extends ComponentCommander {

    /**
     * @param data a string representing the boolean enabled state.
     * @return the number of GUI component with the given enabled state.
     */
    @Override
    Integer executeCommand(int timeout, String componentName, Object... data) {
        int counter = 0;
        List<Container> superContainers = new ArrayList<>();
        for (Window w : Window.getWindows()) {
            if (!superContainers.contains(w)) {
                superContainers.add(w);
            }
        }

        boolean isEnable = Boolean.parseBoolean(componentName);
        for (Container c : superContainers) {
            counter += getEnabledComponentCount(isEnable, c);
        }
        return counter;
    }

    protected int getEnabledComponentCount(boolean isEnabled, Container c) {
        int counter = 0;
        if (c.isEnabled() == isEnabled) {
            counter++;
        }
        for (int i = 0; i < c.getComponentCount(); i++) {
            Component child = c.getComponent(i);
            if (child instanceof Container) {
                counter += getEnabledComponentCount(isEnabled, (Container) child);
            } else {
                counter += child.isEnabled() == isEnabled ? 1 : 0;
            }
        }
        return counter;
    }
}
