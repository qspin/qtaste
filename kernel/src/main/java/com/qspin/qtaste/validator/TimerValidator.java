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

/*
 * TimerValidator.java
 *
 * Created on 6 mars 2008, 14:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.qspin.qtaste.validator;

/**
 * @author lvboque
 */
public class TimerValidator extends Validator {
    private long start;
    private long acceptableMillis;
    private long end;

    /**
     * Creates a new instance of TimerValidator
     */
    public TimerValidator(long acceptableMillis) {
        this.acceptableMillis = acceptableMillis;
        this.start = System.currentTimeMillis();
    }

    protected boolean validate() {
        this.end = System.currentTimeMillis();
        return ((end - start) <= acceptableMillis);
    }

    protected String getExtraDetails() {
        if (validate()) {
            return "";
        } else {
            return "TimerValidator expect to expire before " + acceptableMillis + " but expires after " + (this.end - this.start);
        }
    }
}
