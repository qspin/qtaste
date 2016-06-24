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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.qspin.qtaste.kernel.testapi;

import java.util.Collection;

import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.TestData;

/**
 * A ComponentFactory is responsible to manage the creation of TestAPIComponent instances.
 *
 * @author lvboque
 */
public interface ComponentFactory {
    /**
     * Gets an instance of the component specified as parameter. The returned instance may be linked to a field specified in the
     * TestData.
     *
     * @param component the name of the component
     * @param data the testdata
     * @return the instance of the component or null if the component specified doesn't exist
     * @throws com.qspin.qtaste.testsuite.QTasteException if the component cannot be instantiated
     */
    public Component getComponentInstance(String component, TestData data) throws QTasteException;

    /**
     * Gets all instances of components instantiated by the factory.
     *
     * @return a Collection of the instantiated TestAPIComponent
     */
    public Collection<Component> getComponentsInstances();

    /**
     * Removes an instance of component.
     *
     * @param component component instance to remove
     */
    public void removeComponentInstance(Component component);
}
