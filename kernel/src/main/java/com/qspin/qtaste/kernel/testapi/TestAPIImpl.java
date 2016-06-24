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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.TestData;
import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 * @author lvboque
 */
public class TestAPIImpl implements TestAPI {

    private static Logger logger = Log4jLoggerFactory.getLogger(TestAPIImpl.class);
    // key is component name, value is ManagerVerbs instance
    private HashMap<String, FactoryVerbs> map;
    private static TestAPIImpl instance = null;

    // not public as only TestAPIFactory can create such instance!
    private TestAPIImpl() {
        map = new HashMap<String, FactoryVerbs>();
    }

    public static TestAPI getInstance() {
        if (instance == null) {
            instance = new TestAPIImpl();
        }
        return instance;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Only the ComponentsLoader is supposed to call this method.
     */
    public void register(String packageName, String component, ComponentFactory factory, String method) {
        ArrayList<String> verbs;
        if (!map.containsKey(component)) {
            FactoryVerbs fv = new FactoryVerbs();
            fv.factory = factory;
            fv.verbs = new ArrayList<String>();
            fv.packageName = packageName;
            map.put(component, fv);
            verbs = fv.verbs;
        } else {
            FactoryVerbs fv = map.get(component);
            verbs = fv.verbs;
        }

        if (!verbs.contains(method)) {
            logger.trace("Method " + method + " has been registered to component " + component);
            verbs.add(method);
        } else {
            logger.warn("The method " + method + " is already registered for the component " + component);
        }
    }

    public void unregisterAllMethods() {
        map.clear();
    }

    public Method getMethod(String componentName, String methodName) {
        String classString = componentName;
        try {
            FactoryVerbs fv = map.get(componentName);
            if (fv == null) {
                return null;
            }

            classString = fv.packageName + "." + componentName;

            Class<?> componentClass = Class.forName(classString);
            for (Method method : componentClass.getMethods()) {
                if (method.getName().equals(methodName)) {
                    return method;
                }
            }
            throw new NoSuchMethodException("No method " + methodName + " in component " + componentName);
        } catch (ClassNotFoundException ex) {
            logger.error("Class " + classString + " not found");
            return null;
        } catch (NoSuchMethodException ex) {
            logger.error("Method " + methodName + " of component " + componentName + " not found");
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Collection<String> getRegisteredComponents() {
        return map.keySet();
    }

    /**
     * {@inheritDoc}
     */
    public Collection<String> getRegisteredVerbs(String component) throws NoSuchElementException {
        FactoryVerbs fv = map.get(component);
        if (fv == null) {
            throw new NoSuchElementException("component" + component + " doesn't exist!");
        }
        return fv.verbs;
    }

    /**
     * {@inheritDoc}
     */
    public Component getComponent(String component, TestData data) throws QTasteException {
        FactoryVerbs fv = map.get(component);
        if (fv == null) {
            throw new NoSuchElementException("component" + component + " doesn't exist!");
        }
        return fv.factory.getComponentInstance(component, data);
    }

    public Class<?> getInterfaceClass(Class<?> implementationClass) throws ClassNotFoundException {
        Class<?>[] interfaces = implementationClass.getInterfaces();
        for (Class<?> interfaceClass : interfaces) {

            if ((interfaceClass != Component.class) && (Component.class.isAssignableFrom(implementationClass))) {
                return interfaceClass;
            }
        }
        throw new ClassNotFoundException("No Test API interface found for class " + implementationClass.getName());
    }

    /**
     * {@inheritDoc}
     */
    public String getComponentName(Component component) throws NoSuchElementException {
        try {
            Class<?> interfaceClass = getInterfaceClass(component.getClass());
            String componentName = interfaceClass.getSimpleName();
            try {
                MultipleInstancesComponent trc = (MultipleInstancesComponent) component;
                componentName += "(" + trc.getInstanceId() + ")";
            } catch (ClassCastException e) {
            }
            return componentName;
        } catch (ClassNotFoundException e) {
            throw new NoSuchElementException("component" + component + " doesn't exist!");
        }
    }

    public ComponentFactory getComponentFactory(String componentName) throws NoSuchElementException {
        FactoryVerbs fv = map.get(componentName);
        if (fv == null) {
            throw new NoSuchElementException("component" + componentName + " doesn't exist!");
        } else {
            return fv.factory;
        }
    }

    private class FactoryVerbs {

        ComponentFactory factory;
        String packageName;
        ArrayList<String> verbs;
    }

    public void initializeComponents() {
        SingletonComponentFactory singletonComponentFactory = SingletonComponentFactory.getInstance();
        List<Component> componentsToBeRemoved = new ArrayList<Component>();
        for (Component component : singletonComponentFactory.getComponentsInstances()) {
            try {
                component.initialize();
            } catch (QTasteException e) {
                logger.warn("Couldn't initialize component " + component.getClass().getSimpleName() + ": " + e.getMessage()
                      + ".\nInstance will be deleted.");
                componentsToBeRemoved.add(component);
            }
        }
        for (Component component : componentsToBeRemoved) {
            singletonComponentFactory.removeComponentInstance(component);
        }

        componentsToBeRemoved.clear();
        MultipleInstancesComponentFactory multipleInstancesComponentFactory = MultipleInstancesComponentFactory.getInstance();
        for (Component component : multipleInstancesComponentFactory.getComponentsInstances()) {
            try {
                component.initialize();
            } catch (QTasteException e) {
                componentsToBeRemoved.add(component);
                logger.warn("Couldn't initialize component " + component.getClass().getSimpleName() + ": " + e.getMessage()
                      + ".\nInstance will be deleted.");
            }
        }
        for (Component component : componentsToBeRemoved) {
            multipleInstancesComponentFactory.removeComponentInstance(component);
        }
    }

    public void terminateComponents() {
        ArrayList<Component> components = new ArrayList<Component>();
        components.addAll(SingletonComponentFactory.getInstance().getComponentsInstances());
        components.addAll(MultipleInstancesComponentFactory.getInstance().getComponentsInstances());
        for (Component component : components) {
            try {
                component.terminate();
            } catch (QTasteException e) {
                logger.warn("Couldn't terminate component " + component.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    }
}
