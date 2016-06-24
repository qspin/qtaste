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

import javafx.scene.control.TextInputControl;

import com.qspin.qtaste.testsuite.QTasteTestFailException;

class TextSetter extends UpdateComponentCommander {

    @Override
    protected void doActionsInEventThread() throws QTasteTestFailException {
        final String value = mData[0].toString();

        if (component instanceof TextInputControl) {
            final TextInputControl t = (TextInputControl) component;
            t.requestFocus();
            t.setText(value);
        } else {
            throw new QTasteTestFailException("JavaGUI-FX cannot set text for such component " + component.getClass().getName());
        }
    }

    @Override
    protected void prepareActions() throws QTasteTestFailException {
    }

}
