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

import javax.swing.JComboBox;
import javax.swing.JList;

import com.qspin.qtaste.testsuite.QTasteTestFailException;

/**
 * Component controller responsible for the selection of a specific index on a {@link JComboBox} or on a {@link JList}.
 * This selection is processed by the Swing Thread.
 * 
 * @author simjan
 *
 */
class IndexSelector extends UpdateComponentCommander {

	protected int mIndex;
	
	/**
	 * Takes the index in the user data (the second parameter) and checks the component's type.
	 * @throws QTasteTestFailException if the component is not a JComboBox or a JList.
	 */
	@Override
	protected void prepareActions() throws QTasteTestFailException {
		mIndex = Integer.parseInt(mData[2].toString());
		if (component instanceof JComboBox) {
			JComboBox combo = (JComboBox) component;
			if (combo.getItemCount() < mIndex) {
				throw new QTasteTestFailException("Specified index is out of bounds");
			}
		} else if (component instanceof JList) {
			JList list = (JList) component;
			if (list.getModel().getSize() < mIndex) {
				throw new QTasteTestFailException("Specified index is out of bounds");
			}
		} else {
			throw new QTasteTestFailException("Unsupported component");
		}
	}

	/**
	 * Selects the index.
	 */
	@Override
	protected void doActionsInSwingThread() {
		if (component instanceof JComboBox) {
			((JComboBox) component).setSelectedIndex(mIndex);
		} else if (component instanceof JList) {
			((JList) component).setSelectedIndex(mIndex);
		}
	}

}
