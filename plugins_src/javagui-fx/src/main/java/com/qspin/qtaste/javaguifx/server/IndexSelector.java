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

import com.qspin.qtaste.testsuite.QTasteTestFailException;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;

/**
 * Component controller responsible for the selection of a specific index on a {@link ComboBox} or on a {@link ListView}.
 * This selection is processed by the Swing Thread.
 *
 * @author simjan
 *
 */
class IndexSelector extends UpdateComponentCommander {

	protected int mIndex;

	/**
	 * Takes the index in the user data (the second parameter) and checks the component's type.
	 * @throws QTasteTestFailException if the component is not a ComboBox or a ListView.
	 */
	@Override
	protected void prepareActions() throws QTasteTestFailException {
		mIndex = Integer.parseInt(mData[0].toString());
		if (component instanceof ComboBox) {
			ComboBox<?> combo = (ComboBox<?>) component;
			if (combo.getItems().size() < mIndex) {
				throw new QTasteTestFailException("Specified index is out of bounds");
			}
		} else if (component instanceof ListView) {
			ListView<?> list = (ListView<?>) component;
			if (list.getItems().size() < mIndex) {
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
	protected void doActionsInEventThread() {
		if (component instanceof ComboBox) {
			((ComboBox<?>) component).getSelectionModel().select(mIndex);
		} else if (component instanceof ListView) {
			((ListView<?>) component).getSelectionModel().select(mIndex);
		}
	}

}
