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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.qspin.qtaste.config.TestBedConfiguration;
import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.TestData;
import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 * SingletonFactory is a Factory able to create only one instance of a TestAPIComponent.
 * @author lvboque
 */
public class SingletonComponentFactory implements ComponentFactory {

    private static Logger logger = Log4jLoggerFactory.getLogger(SingletonComponentFactory.class);
    private static SingletonComponentFactory instance = null;
    private HashMap<String, Component> map;
    private TestBedConfiguration testbedConfig;

    private SingletonComponentFactory() {
        map = new HashMap<String, Component>();
        testbedConfig = TestBedConfiguration.getInstance();

        TestBedConfiguration.registerConfigurationChangeHandler(new TestBedConfiguration.ConfigurationChangeHandler() {

            public void onConfigurationChange() {
                testbedConfig = TestBedConfiguration.getInstance();
                map.clear();
            }
        });
    }

    /**
     * Get an instance of the SingletonFactory.
     * @return The SingletonFactory.
     */
    synchronized public static SingletonComponentFactory getInstance() {
        if (instance == null) {
            instance = new SingletonComponentFactory();
        }
        return instance;
    }

    /**
     * Return the instance of the component specified as parameter.
     * @param component the component name
     * @param data the data. Not used for this type of factory
     * @return the instance of the component or null if the component specified doesn't exist
     * @throws com.qspin.qtaste.testsuite.QTasteException if the component cannot be instantiated
     */
    public Component getComponentInstance(String component, TestData data) throws QTasteException {
        if (map.containsKey(component)) {
            return map.get(component);
        } else {
            // check if the component must be loaded
            List<?> list = testbedConfig.configurationsAt("singleton_components." + component);
            if (!list.isEmpty()) {
                Class<?> c = ComponentsLoader.getInstance().getComponentImplementationClass(component);
                if (c == null) {
                    throw new QTasteException("The class implemeting the component " + component + " is not registered in platform selected in the Testbed configuration.");
                }
                Component componentImpl = createComponentInstance(c);
                map.put(component, componentImpl);
                return componentImpl;
            } else {
                logger.warn("Component " + component + " is not loaded because not defined in the testbed configuration.");
                return null;
            }
        }
    }

    public Collection<Component> getComponentsInstances() {
        return map.values();
    }
    
    public void removeComponentInstance(Component component) {
    	for (Map.Entry<String, Component> entry: map.entrySet()) {
    		if (entry.getValue() == component) {
    			map.remove(entry.getKey());
    			return;
    		}
    	}
    	logger.error("Component instance not found");
    }

    private Component createComponentInstance(Class<?> componentClass) throws QTasteException {
        try {
            Component componentImpl = (Component) componentClass.newInstance();
            return componentImpl;
        } catch (InstantiationException e) {
            logger.error("SingletonFactory cannot instantiate the constructor of " + componentClass.getName(), e);
        } catch (IllegalAccessException e) {
            logger.error("SingletonFactory cannot access the constructor of " + componentClass.getName(), e);
        }
        throw new QTasteException("ComponentClass " + componentClass + " cannot be instantiated");
    }
}
