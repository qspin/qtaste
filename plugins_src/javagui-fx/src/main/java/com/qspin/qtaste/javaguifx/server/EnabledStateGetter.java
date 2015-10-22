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

import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

import javafx.scene.Node;

/**
 * Component asker which return the enabled stated of a component.
 * @author simjan
 *
 */
class EnabledStateGetter extends ComponentCommander {

	/**
	 * @param data the component's name.
	 * @return <code>true</code> if the component is enabled.
	 * @throws QTasteTestFailException if no component is found.
	 */
	@Override
	Boolean executeCommand(int timeout, String componentName, Object... data) throws QTasteException {
		Node c = getComponentByName(componentName);
		return c==null?false:!c.isDisabled();
	}

}
