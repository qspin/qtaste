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

package com.qspin.qtaste.kernel.testapi;

import junit.framework.TestCase;
import org.apache.log4j.PropertyConfigurator;

import com.qspin.qtaste.config.StaticConfiguration;
import com.qspin.qtaste.config.TestBedConfiguration;

/**
 * @author lvboque
 */
public class ComponentsLoaderTest extends TestCase {

    public ComponentsLoaderTest(String testName) {
        super(testName);
        // Log4j Configuration
        PropertyConfigurator.configure(StaticConfiguration.CONFIG_DIRECTORY + "/log4j.properties");
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        TestBedConfiguration.setConfigFile(
              StaticConfiguration.TESTBED_CONFIG_DIRECTORY + "/enginetest." + StaticConfiguration.TESTBED_CONFIG_FILE_EXTENSION);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of getInstance method, of class ComponentsLoader.
     */
    public void testGetInstance() {
        System.out.println("getInstance");
        ComponentsLoader result = ComponentsLoader.getInstance();
        assertNotNull("instance cannot be null", result);

        ComponentsLoader result2 = ComponentsLoader.getInstance();

        assertNotNull(result2);

        assertEquals("should get the same instance", result, result2);
    }

    /**
     * Test of getComponentImplementationClass method, of class ComponentsLoader.
     */
    public void testGetComponentImplementationClass() {
        System.out.println("getComponentImplementationClass");
        String component = "";
        ComponentsLoader instance = ComponentsLoader.getInstance();

        // Get a non-existing component
        Class<?> result = instance.getComponentImplementationClass(component);
        assertNull(result);

        // Get an instance of the EngineTest component
        Class<?> result2 = instance.getComponentImplementationClass("EngineTest");
        assertNotNull(result2);
    }
}
