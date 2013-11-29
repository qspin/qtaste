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

package com.qspin.qtaste.validator;

import com.qspin.qtaste.testsuite.QTasteTestFailException;

public class ReturnCodeValidator extends Validator {

    private int expected;
    private int actual;

    public static void check(int expected, int actual) throws QTasteTestFailException {
        check(expected, actual, null);
    }

    public static void check(int expected, int actual, String failMessagePrefix) throws QTasteTestFailException {
        ReturnCodeValidator validator = new ReturnCodeValidator(expected, actual);
        validator.validate(failMessagePrefix);
    }

    private ReturnCodeValidator(int expected, int actual) {
        super();
        this.expected = expected;
        this.actual = actual;
    }

    protected boolean validate() {
        return expected == actual;
    }

    protected String getExtraDetails() {
        String output = new String("Return code expected " + expected);
        if (validate()) {
            output += " and";
        } else {
            output += " but";
        }

        output += " got " + actual;
        return output;
    }
}
