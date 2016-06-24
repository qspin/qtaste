/*
    Copyright 2007-2009 QSpin - www.qspin.be

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

package com.qspin.qtaste.event;

import java.util.EventObject;

/**
 * @author vdubois
 *         This class is defined to send event from and to the UI when QTaste is in debug mode
 */
@SuppressWarnings("serial")
public class TestScriptBreakpointEvent extends EventObject {
    public enum Action {BREAK, CONTINUE, STEP, STOP, DUMP_STACK, DUMP_VAR, CALL_METHOD, STEPINTO}

    private Action mAction;
    private Object mExtraData;

    public TestScriptBreakpointEvent(Object source, Action startStop, Object extraData) {
        super(source);
        mAction = startStop;
        mExtraData = extraData;

    }

    public Action getAction() {
        return mAction;
    }

    public Object getExtraData() {
        return mExtraData;
    }
}
