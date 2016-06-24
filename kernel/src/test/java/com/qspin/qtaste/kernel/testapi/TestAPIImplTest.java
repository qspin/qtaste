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

import java.util.Collection;
import java.util.NoSuchElementException;

import junit.framework.TestCase;
import org.apache.log4j.PropertyConfigurator;

import com.qspin.qtaste.config.StaticConfiguration;
import com.qspin.qtaste.config.TestBedConfiguration;
import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.TestData;

/**
 * @author lvboque
 */
public class TestAPIImplTest extends TestCase {
    private TestAPI instance;

    public TestAPIImplTest(String testName) {
        super(testName);
        // Log4j Configuration
        String path = StaticConfiguration.CONFIG_DIRECTORY + "/log4j.properties";
        PropertyConfigurator.configure(path.replaceAll(" ", "\\ "));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // register component into the TestAPI
        String path =
              StaticConfiguration.TESTBED_CONFIG_DIRECTORY + "/enginetest." + StaticConfiguration.TESTBED_CONFIG_FILE_EXTENSION;
        TestBedConfiguration.setConfigFile(path.replaceAll(" ", "\\ "));
        ComponentsLoader.getInstance();
        instance = TestAPIImpl.getInstance();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        instance = null;
    }

    /**
     * Test of register and getRegisteredComponents method, of class TestAPIImpl.
     */
    public void testRegisterAndGetRegisteredComponents() {
        System.out.println("register");
        String packageName = "JUNIT";
        String component = "JUNIT";
        ComponentFactory factory = null;
        String method = "test";

        instance.register(packageName, component, factory, method);

        System.out.println("getRegisteredComponents");
        Collection<String> result = instance.getRegisteredComponents();

        assertNotNull(result);

        assertTrue("JUNIT should be a component as it has been previously added", result.contains("JUNIT"));
    }

    /**
     * Test of getRegisteredVerbs method, of class TestAPIImpl.
     */
    public void testGetRegisteredVerbs() {
        System.out.println("getRegisteredVerbs");
        String component = "nonexistingcomponent";

        try {
            instance.getRegisteredVerbs(component);
            fail("should throw a NoSuchElementException as component is not existing");
        } catch (NoSuchElementException e) {
            // should be catched
        }

        component = "EngineTest";
        Collection<String> result = instance.getRegisteredVerbs(component);
        assertTrue("A existing component should contains should contain methods", result.size() > 0);
    }

    /**
     * Test of getComponent method, of class TestAPIImpl.
     */
    public void testGetComponent() {
        System.out.println("getComponent");
        String component = "non-existingcomponent";
        TestData data = null;

        try {
            instance.getComponent(component, data);
            fail("getComponent should throw a NoSuchElementException as component doesn't exist!");
        } catch (NoSuchElementException e) {
            // should be catched
        } catch (QTasteException e) {
            fail("getComponent should throw a NoSuchElementException as component doesn't exist!");
        }

        // Please notice that EngineTest component use a SingletonFactory so data is not used!
        component = "EngineTest";
        try {
            Component result = instance.getComponent(component, data);
            assertNotNull("The instance of EngineTest should not be null", result);
        } catch (QTasteException e) {
            fail("getComponent should not throw an QTasteException as fail as component can be instantiated!");
        }
    }

    /**
     * Test of getComponentName method, of class TestAPIImpl.
     */
    public void testGetComponentName() {
        System.out.println("getComponentName");

        // Please notice that EngineTest component use a SingletonFactory so data is not used!
        String component = "EngineTest";
        TestData data = null;
        try {
            Component result = instance.getComponent(component, data);
            String name = instance.getComponentName(result);
            assertTrue("The name of the component is not correct!", name.equals(component));
        } catch (QTasteException e) {
            fail("getComponent should not throw an QTasteException as fail as component can be instantiated!");
        }
    }
}
