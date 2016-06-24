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

import javafx.scene.Node;

import com.qspin.qtaste.testsuite.QTasteException;

public class ComponentRawNameGetter extends ComponentCommander {

    @Override
    String executeCommand(int timeout, String componentName, Object... data) throws QTasteException {
        Node c = getComponentByName(componentName);
        if (c != null) {
            return c.getId();
        }
        return null;
    }

}
