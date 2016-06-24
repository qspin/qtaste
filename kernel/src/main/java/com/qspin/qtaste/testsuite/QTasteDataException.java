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
 * QTasteDataException represents a error related to the test data.
 * Nothing about the test behavior or the testAPI, just test data.
 * If there is a problem related to an invalid data type or an unexpected value.
 * For example, if a test expects a value between 0 and 10 but the data is not present in the csv file, then a
 * QTasteDataException
 * will be thrown.
 *
 * @author dergo
 */
@SuppressWarnings("serial")
public class QTasteDataException extends QTasteException {

    /**
     * Creates a new instance of QTasteDataException
     */
    public QTasteDataException(String message) {
        super(message);
    }

    public QTasteDataException(String message, Throwable e) {
        super(message, e);
    }
}
