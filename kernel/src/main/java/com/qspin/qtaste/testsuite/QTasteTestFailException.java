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

package com.qspin.qtaste.testsuite;

/**
 * QTasteTestFailException are thrown by testAPI component.
 * This kind of exception is thrown, for example, when the testAPI is in an invalid state and 
 * the current cannot be continued.  
 * @author dergo
 */
@SuppressWarnings("serial")
public class QTasteTestFailException extends QTasteException {
    
    /** Creates a new instance of QTASTEFailException */
    public QTasteTestFailException(String message) {
        super(message);
    }
    
    public QTasteTestFailException(String message, Throwable e) {
        super(message, e);
    }
}
