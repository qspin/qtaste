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
import java.util.Collection;
import java.util.NoSuchElementException;

import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.TestData;

/**
 * The TestAPI is the interface to interact with TestAPIComponent objects and invoke verbs of TestAPIComponent
 *
 * @author lvb
 */
public interface TestAPI {

    /**
     * Get the first method with given name of given component
     *
     * @param componentName The specified component name
     * @param methodName The specified method name
     * @return the first Method with given name of given component or null if the component or method is not found
     */
    public Method getMethod(String componentName, String methodName);

    /**
     * Register the specified method of the specified component and the associated manager
     *
     * @param packageName the package name
     * @param component the component name
     * @param manager the manager associated to this method
     * @param method the method to register
     */
    public void register(String packageName, String component, ComponentFactory manager, String method);

    /**
     * Unregister all methods
     */
    public void unregisterAllMethods();

    /**
     * Get an Iterator containing all the names of the registered components
     *
     * @return a Collection of String
     */
    public Collection<String> getRegisteredComponents();

    /**
     * Get the list of verbs available within the specified component
     *
     * @param componentName the specified component
     * @return the Collection of String or null if the component doesn't exist
     */

    public Collection<String> getRegisteredVerbs(String componentName);

    /**
     * Return the instance of the component specified as parameter.
     *
     * @param componentName the component name
     * @param data the TestData
     * @return the component instance
     * @throws java.util.NoSuchElementException if component doesn't exist
     * @throws com.qspin.qtaste.testsuite.QTasteException if component cannot be instantiated
     */
    public Component getComponent(String componentName, TestData data) throws NoSuchElementException, QTasteException;

    /**
     * Return the test api component interface class of the given implementation class.
     *
     * @param implementationClass test api component implementation class
     * @return test api component interface class of the given implementation class
     * @throws java.lang.ClassNotFoundException if the test api interface class of the given implementation class is not found
     */
    public Class<?> getInterfaceClass(Class<?> implementationClass) throws ClassNotFoundException;

    /**
     * Return the name of the given component.
     *
     * @param component Test API component
     * @return name of component
     * @throws java.util.NoSuchElementException if component doesn't exist
     */
    public String getComponentName(Component component) throws NoSuchElementException;

    /**
     * Return the component factory of the given component.
     *
     * @param componentName Test API component name
     * @return component factory of the given component
     * @throws java.util.NoSuchElementException if component doesn't exist
     */
    public ComponentFactory getComponentFactory(String componentName) throws NoSuchElementException;

    /**
     * Initializes all instantiated components.
     * If a component cannot be initialized, it is unregistered from its factory.
     */
    public void initializeComponents();

    /**
     * Terminates all instantiated components.
     */
    public void terminateComponents();
}
