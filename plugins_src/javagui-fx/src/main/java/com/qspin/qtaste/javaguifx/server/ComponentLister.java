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

package com.qspin.qtaste.javaguifx.server;

import java.awt.Window;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.javafx.stage.StageHelper;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;

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

        for (Stage s : StageHelper.getStages()) {
            browseComponent(s.getScene().getRoot().getChildrenUnmodifiable());
        }

        ArrayList<String> list = new ArrayList<>();
        for (String key : mComponentsMap.keySet()) {
            list.add(key + "   (number of instance with this name :" + mComponentsMap.get(key).size() + ")");
        }
        Collections.sort(list);
        list.add("Number of ownerless windows : " + Window.getOwnerlessWindows().length);
        list.add("Number of windows : " + Window.getWindows().length);
        list.add("Number of windows (JavaFX) : " + StageHelper.getStages().size());
        return list.toArray(new String[list.size()]);
    }

    private void addToMap(Node c) {
        String componentName = c.getId();
        if (!mComponentsMap.containsKey(componentName)) {
            mComponentsMap.put(componentName, new ArrayList<>());
        }
        if (!mComponentsMap.get(componentName).contains(c)) {
            mComponentsMap.get(componentName).add(c);
        }
    }

    private void browseComponent(ObservableList<Node> components) {
        for (Node n : components) {
            String componentName = n.getId();
            // LOGGER.debug("browsing " + components[c].toString());
            // LOGGER.debug("name=" + componentName);
            if (componentName != null) {
                //LOGGER.debug("Component:" + componentName + " is found!");
                //if (!componentName.startsWith("null."))
                addToMap(n);
            }
            if (n instanceof Parent) {
                browseComponent(((Parent) n).getChildrenUnmodifiable());
            }
        }
    }

    private Map<String, List<Node>> mComponentsMap;
}
