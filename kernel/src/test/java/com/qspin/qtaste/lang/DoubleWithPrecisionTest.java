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

package com.qspin.qtaste.lang;

import com.qspin.qtaste.lang.DoubleWithPrecision;
import junit.framework.TestCase;

/**
 *
 * @author dergo
 */
public class DoubleWithPrecisionTest extends TestCase {

    private DoubleWithPrecision instance;

    public DoubleWithPrecisionTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        instance = new DoubleWithPrecision("1.0(0.5)");
        System.out.println(instance);
    }

    @Override
    protected void tearDown() throws Exception {
        instance = null;
        super.tearDown();
    }

    public void testCompareWithInteger() {
        System.out.println("testCompareWithInteger");
        assertTrue(instance.equals(new Integer(1)));
        assertFalse(instance.equals(new Integer(2)));
    }

    public void testCompareWithDouble() {
        System.out.println("testCompareWithDouble");
        assertTrue(instance.equals(new Double(1.0)));
        assertTrue(instance.equals(new Double(0.5)));
        assertTrue(instance.equals(new Double(1.5)));
        assertFalse(instance.equals(new Double(0.49)));
        assertFalse(instance.equals(new Double(1.51)));
    }

    public void testCompareWithDoubleWithPrecision() {
        System.out.println("testCompareWithDoubleWithPrecision");
        assertTrue(instance.equals(new DoubleWithPrecision(1.0, 0.01)));
        assertTrue(instance.equals(new DoubleWithPrecision(0.49, 0.01)));
        assertTrue(instance.equals(new DoubleWithPrecision(1.51, 0.01)));
        assertFalse(instance.equals(new DoubleWithPrecision(0.4, 0.01)));
        assertFalse(instance.equals(new DoubleWithPrecision(1.6, 0.01)));
    }
}
