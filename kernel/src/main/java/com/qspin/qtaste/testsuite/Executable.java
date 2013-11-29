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
 * Executable.java
 *
 * Created on 16 octobre 2007, 13:56
 */

package com.qspin.qtaste.testsuite;

import com.qspin.qtaste.reporter.testresults.TestResult;

/**
 *
 * @author lvboque
 */
public interface Executable {    
    boolean execute(TestData data, TestResult result, boolean debug);
}
