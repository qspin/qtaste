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

/**
 * Interface for all Test API multiple instances components
 * 
 * @author David Ergo
 */
public interface MultipleInstancesComponent extends Component {

    /**
     * This component uses a MultipleInstancesFactory. <br>
     * The constructors of implementing classes must have one <code>String</code>
     * parameter, the instance id. <br>
     * The id of the instance is specified by the <code>INSTANCE_ID</code> test data.
     */
    public static final ComponentFactory factory = MultipleInstancesComponentFactory.getInstance();
    
    public String getInstanceId();
}
