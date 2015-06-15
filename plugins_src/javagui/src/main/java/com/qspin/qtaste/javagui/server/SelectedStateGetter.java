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

import javax.swing.AbstractButton;

import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

/**
 * Component asker which return the selection stated of an {@link AbstractButton}.
 * @author simjan
 *
 */
class SelectedStateGetter extends ComponentCommander {

	/**
	 * @param data the component's name.
	 * @return <code>true</code> if the component is select.
	 * @throws QTasteTestFailException if no component is found or if it's not an AbstractButton.
	 */
	@Override
	Boolean executeCommand(int timeout, String componentName, Object... data) throws QTasteException {
		Component c = getComponentByName(componentName);
		if ( c instanceof AbstractButton )
			return ((AbstractButton) c).isSelected();
		else
			throw new QTasteTestFailException("The component does not extends AbstractButton");
	}

}
