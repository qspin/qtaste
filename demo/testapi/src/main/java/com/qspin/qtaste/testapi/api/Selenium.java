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

package com.qspin.qtaste.testapi.api;

import com.qspin.qtaste.kernel.testapi.MultipleInstancesComponent;

/**
 * Selenium is the interface of the QTaste Test API component providing verbs
 * for testing web sites using Selenium interface.
 *
 * @author Laurent Vanboquestal
 */
public interface Selenium extends com.thoughtworks.selenium.Selenium, MultipleInstancesComponent {

    /**
     * Open the web browser specified as argument.
     * the command string used to launch the browser, e.g. "*firefox", "*iexplore" or "c:\\program files\\internet
     * explorer\\iexplore.exe"
     *
     * @param browser the browser String
     */
    public void openBrowser(String browser);

    /**
     * Close the web browser
     */
    public void closeBrowser();

}
