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

package com.iba.ate.testapi.api;import com.iba.ate.kernel.testapi.MultipleInstancesComponent;import com.iba.ate.testsuite.QTasteException;import com.iba.ate.testsuite.RequiredData;import com.iba.ate.testsuite.TestData;/** * [$COMPONENT_NAME]  is the interface of the QTaste Test API component providing verbs * <p> * * @author Vincent Dubois */public interface [$COMPONENT_NAME] extends MultipleInstancesComponent {            /**           * Calls the <code>myMethod</code> method of the [$COMPONENT_NAME] .	* Prepare the bds service for a new irradiation. 	* @data myData [String] 	*/    @RequiredData({"DATA1","DATA2"})    public void myMethod(TestData data) throws QTasteException;}