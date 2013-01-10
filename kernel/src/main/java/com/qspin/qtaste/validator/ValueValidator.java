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
 * ValueValidator.java
 *
 * Created on 8 novembre 2007, 17:33
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.qspin.qtaste.validator;

import com.qspin.qtaste.testsuite.QTasteTestFailException;

/**
 *
 * @author lvboque
 */
public class ValueValidator extends Validator {
    private Object actual;
    private Object expected;

    
    public static void check(Object expected, Object actual) throws QTasteTestFailException {
        check(expected, actual, null);
    }
    
    public static void check(Object expected, Object actual, String failMessagePrefix) throws QTasteTestFailException {
        ValueValidator validator = new ValueValidator(expected, actual);
        validator.validate(failMessagePrefix);
    }

    /** Creates a new instance of ValueValidator */
    private ValueValidator(Object expected, Object actual) {
        this.expected = expected;
        this.actual = actual;
    }
    
    protected boolean validate() {
        return actual.toString().equals(expected.toString());
    }
    
    protected String getExtraDetails() {
        if (validate())
            return "";
        else
            return "ValueValidator expect: " + expected.toString() + " but got: " + actual.toString();
    }
}
