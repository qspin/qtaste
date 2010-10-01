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

import java.lang.reflect.InvocationTargetException;
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
 * MultipleInstancesFactory is a Factory able to create several instances of a TestAPIComponent based on the 'INSTANCE_ID' field.
 * @author lvboque
 */
public class MultipleInstancesComponentFactory implements ComponentFactory {

    private static Logger logger = Log4jLoggerFactory.getLogger(MultipleInstancesComponentFactory.class);
    private static MultipleInstancesComponentFactory instance = null;
    private TestBedConfiguration testbedConfig;
    // key is component name and the INSTANCE_ID, value is TestAPIComponent
    private HashMap<String, Component> map;

    private MultipleInstancesComponentFactory() {
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
     * Get an instance of the MultipleInstancesFactory.
     * @return The MultipleInstancesFactory.
     */
    synchronized public static MultipleInstancesComponentFactory getInstance() {
        if (instance == null) {
            instance = new MultipleInstancesComponentFactory();
        }
        return instance;
    }

    /**
     * Return the instance of the component associated to the INSTANCE_ID field of the TestData. 
     * If the INSTANCE_ID is not present in the TestData, the default treatment_room of the TestBedConfiguration will be used.     
     * @param component the specified component
     * @param data the testdata
     * @return the instance of the component.
     * @throws com.qspin.qtaste.testsuite.QTasteException if the component cannot be instantiated
     */
    public Component getComponentInstance(String component, TestData data) throws QTasteException {
        String instanceId = data.getValue("INSTANCE_ID");

        String key = component + instanceId;
        if (map.containsKey(key)) {
            return map.get(key);
        } else {
            // check if the component is defined in the testbed configuration            
            int instanceIndex = testbedConfig.getMIIndex(instanceId, component);            
            String keyToFind = "multiple_instances_components." + component + "(" + instanceIndex + ")";            
            List<?> list = testbedConfig.configurationsAt(keyToFind);            
            if (!list.isEmpty()) {                
                Class<?> c = ComponentsLoader.getInstance().getComponentImplementationClass(component);
                if (c == null) {
                    throw new QTasteException("The class implementing the component " + component + " is not registered in testapi_implementation.import(s) selected in the Testbed configuration.");
                }                
                Component componentImpl = createComponentInstance(instanceId, c);               
                map.put(key, componentImpl);                
                return componentImpl;
            } else {
                logger.warn("Component " + component + " is not loaded because not defined in the testbed configuration.");
                return null;                
            }
        }
    }

    private Component createComponentInstance(String instanceId, Class<?> component) throws QTasteException {
        try {            
            Component componentImpl = (Component) component.getConstructor(String.class).newInstance(new String(instanceId));            
            return componentImpl;
        } catch (NoSuchMethodException e) {
            logger.error("MultipleInstancesFactory cannot get the constructor of " + component.getName(), e);
        } catch (InstantiationException e) {
            logger.error("MultipleInstancesFactory cannot instantiate the constructor of " + component.getName(), e);
        } catch (IllegalAccessException e) {
            logger.error("MultipleInstancesFactory cannot access the constructor of " + component.getName(), e);
        } catch (InvocationTargetException e) {
            logger.error("MultipleInstancesFactory cannot invoke the constructor of " + component.getName(), e.getTargetException());
            Throwable targetException = e.getTargetException();
            if (targetException instanceof QTasteException) {
                throw (QTasteException)targetException;
            }
        }
        throw new QTasteException("ComponentClass " + component + " cannot be instantiated");
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
}
