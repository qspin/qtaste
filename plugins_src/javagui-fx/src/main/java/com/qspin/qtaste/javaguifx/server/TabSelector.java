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

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import com.qspin.qtaste.testsuite.QTasteTestFailException;

class TabSelector extends UpdateComponentCommander {

    public enum SelectorIdentifier {
        SELECT_BY_INDEX,
        SELECT_BY_TITLE,
        SELECT_BY_COMPONENT_ID
    }

    private SelectorIdentifier mSelectorIdentifier;
    protected Tab mTabToSelect;

    public TabSelector(SelectorIdentifier aSelectorIdentifier) {
        mSelectorIdentifier = aSelectorIdentifier;
    }

    @Override
    protected void prepareActions() throws QTasteTestFailException {

        // sanity checks
        if (mData.length == 0 || mData[0] == null) {
            throw new QTasteTestFailException("No tab index, tab title or tab component id provided!");
        }

        switch (mSelectorIdentifier) {
            case SELECT_BY_INDEX:
                int tabIndex = Integer.parseInt(mData[0].toString());

                if (tabIndex < -1 || tabIndex >= ((TabPane) component).getTabs().size()) {
                    throw new QTasteTestFailException("Tab index " + tabIndex + " out of bounds.");
                }
                mTabToSelect = ((TabPane) component).getTabs().get(tabIndex);
                break;

            case SELECT_BY_TITLE:
                String tabTitle = mData[0].toString();

                for (Tab t : ((TabPane) component).getTabs()) {
                    if (t.getText().equals(tabTitle)) {
                        mTabToSelect = t;
                        break;
                    }
                }

                if (mTabToSelect == null) {
                    throw new QTasteTestFailException("Unable to find tab titled '" + tabTitle + "'");
                }
                break;

            case SELECT_BY_COMPONENT_ID:
                String componentName = mData[0].toString();

                for (Tab t : ((TabPane) component).getTabs()) {
                    if (t.getId().equals(componentName)) {
                        mTabToSelect = t;
                        break;
                    }
                }

                if (mTabToSelect == null) {
                    throw new QTasteTestFailException("Unable to find the component named '" + componentName + "'");
                }
                break;

            default:
                throw new QTasteTestFailException("Bad selector identifier");
        }

    }

    @Override
    protected void doActionsInEventThread() throws QTasteTestFailException {
        if (component != null && component instanceof TabPane) {
            ((TabPane) component).selectionModelProperty().getValue().select(mTabToSelect);
        }
    }
}
