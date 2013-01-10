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

import java.awt.Frame;
import java.awt.Window;

import javax.swing.JOptionPane;

import com.qspin.qtaste.testsuite.QTasteTestFailException;

/**
 *
 * @author lvboque
 */
public class VisualValidator extends Validator {    
    private String message;
    
    
    public static void check(String message) throws QTasteTestFailException {
        check(message, null);
    }
    
    public static void check(String message, String failMessagePrefix) throws QTasteTestFailException {
        VisualValidator validator = new VisualValidator(message);
        validator.validate(failMessagePrefix);
    }    
    
    private VisualValidator(String message) {
        this.message = message;
    }

    protected boolean validate() {
        // request focus
        Frame frame = new Frame();
        Window window = new Window(frame);
        window.requestFocus();
 
        int response = JOptionPane.showConfirmDialog(window, message, "Confirmation", JOptionPane.YES_NO_OPTION);
        return response == 0;        
    }
    
    protected String getExtraDetails() {        
        return "Not yet implemented!";
    }    
}
