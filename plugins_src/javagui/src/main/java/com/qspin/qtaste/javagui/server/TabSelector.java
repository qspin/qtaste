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

import javax.swing.JTabbedPane;

import com.qspin.qtaste.testsuite.QTasteTestFailException;

class TabSelector extends UpdateComponentCommander {

	protected int mTabIndex;
	protected String mTabTitle;

	@Override
	protected void prepareActions() throws QTasteTestFailException {
		if ( mData.length == 0 || mData[0] == null )
			throw new QTasteTestFailException("No tab index or tab title provided!");
		if ( mData[0] instanceof String)
			mTabTitle = mData[0].toString();
		else
			mTabIndex = Integer.parseInt(mData[0].toString());
	}

	@Override
	protected void doActionsInSwingThread() {
		if (component != null && component instanceof JTabbedPane) {
			int index = -1;
			if ( mTabTitle != null )
				index = ((JTabbedPane)component).indexOfTab(mTabTitle);
			else
				index = mTabIndex;
			((JTabbedPane)component).setSelectedIndex(index);
		}
	}

}
