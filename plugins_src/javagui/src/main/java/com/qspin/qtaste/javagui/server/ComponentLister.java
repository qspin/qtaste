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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPopupMenu;

/**
 * Component asker responsible for the listing of all component's names.
 *
 * @author simjan
 */
final class ComponentLister extends ComponentCommander {

    /**
     * Lists all component's names and counts the number of instances which have the same names.
     *
     * @return an array of Strings that have the following format : component_name (number of instance : X)
     */
    @Override
    String[] executeCommand(int timeout, String componentName, Object... data) {
        mComponentsMap = new HashMap<>();

        Window[] displayableWindows = getDisplayableWindows();
        for (Window window : displayableWindows) {
            if (window.getName() != null) {
                addToMap(window);
            }
            browseComponent(window.getComponents());
        }

        ArrayList<String> list = new ArrayList<>();
        for (String key : mComponentsMap.keySet()) {
            list.add(key + "   (number of instance with this name :" + mComponentsMap.get(key).size() + ")");
        }
        Collections.sort(list);
        list.add("Number of ownerless windows : " + Window.getOwnerlessWindows().length);
        list.add("Number of displayable windows : " + displayableWindows.length);
        return list.toArray(new String[list.size()]);
    }

    private void addToMap(Component c) {
        String componentName = c.getName();
        if (!mComponentsMap.containsKey(componentName)) {
            mComponentsMap.put(componentName, new ArrayList<>());
        }
        if (!mComponentsMap.get(componentName).contains(c)) {
            mComponentsMap.get(componentName).add(c);
        }
    }

    private void browseComponent(Component[] components) {
        for (Component component : components) {
            String componentName = component.getName();
            // LOGGER.debug("browsing " + components[c].toString());
            // LOGGER.debug("name=" + componentName);
            if (componentName != null) {
                //LOGGER.debug("Component:" + componentName + " is found!");
                //if (!componentName.startsWith("null."))
                addToMap(component);
            }
            if (component instanceof Container) {
                if (component instanceof JPopupMenu) {
                    LOGGER.debug("detected JPopupMenu !!!!");
                }
                browseComponent(((Container) component).getComponents());
            }
        }
    }

    private Map<String, List<Component>> mComponentsMap;
}
