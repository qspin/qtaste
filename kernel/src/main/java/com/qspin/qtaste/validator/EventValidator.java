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
 * EventValidator.java
 *
 * Created on 7 novembre 2007, 9:43
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
public class EventValidator extends Validator {
    
    public static void check() throws QTasteTestFailException {
        check(null);
    }
    
    public static void check(String failMessagePrefix) throws QTasteTestFailException {
        EventValidator validator = new EventValidator();
        validator.validate(failMessagePrefix);
    }

    /** Creates a new instance of EventValidator */
    private EventValidator() {
    }
        
    protected boolean validate() {        
        return false;
    }
    
    protected String getExtraDetails() {        
        return "Not yet implemented!";
    }    
}
