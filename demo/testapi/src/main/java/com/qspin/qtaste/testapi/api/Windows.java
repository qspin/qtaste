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

import com.qspin.qtaste.kernel.testapi.SingletonComponent;

/**
 * Generic Windows GUI Automation API
 *
 * @author Laurent Vanboquestal
 */
public interface Windows extends SingletonComponent {
    /**
     * Start the application specified as parameter
     *
     * @param name the name of the application (full path to executable)
     * @return -1 if the application cannot be started, a positive value in other cases
     * @throws Exception if an error occurs during the communication with the application
     */
    public void startApplication(String name) throws Exception;

    /**
     * Stop the application
     *
     * @return -1 if the application cannot be stopped, a positive value in other cases
     * @throws Exception if an error occurs during the communication with the application
     */
    public void stopApplication() throws Exception;

    /**
     * Press the specified button on the specified window
     *
     * @param windowName the window name containing the button (i.e: Calculator)
     * @param name the name of the button to be pressed
     * @throws Exception if an error occurs during the communication with the application
     */
    public void pressButton(String windowName, String name) throws Exception;

    /**
     * Select the item of the menu specified as parameter
     *
     * @param windowName the window name containing the menu (i.e: Calculator)
     * @param menu the item to be selected (i.e: Help->AboutCalculator)
     * @throws Exception if an error occurs during the communication with the application
     */
    public void selectMenu(String windowName, String menu) throws Exception;

    /**
     * Return the text of the specified component present in the specified window
     *
     * @param windowName the window name (i.e: Calculator)
     * @param name the name of the component (i.e: Edit)
     * @return The text value identified by the specified component name in the specified window name
     * @throws Exception if an error occurs during the communication with the application
     */
    public String getText(String windowName, String name) throws Exception;

    /**
     * Set the text of a component present on the specified window with the specified value
     *
     * @param windowName the window name (i.e: Calculator)
     * @param name the name of the component (i.e: Edit)
     * @param value the new text value for the component
     * @throws Exception if an error occurs during the communication with the application
     */
    public void setText(String windowName, String name, String value) throws Exception;

    /**
     * Select an item in the specified treeview
     *
     * @param windowName the window name (i.e: Calculator)
     * @param treeviewName the treeview name
     * @param item the item in the treeview to be selected
     * @throws Exception if an error occurs during the communication with the application
     */
    public void selectTreeViewItem(String windowName, String treeviewName, String item) throws Exception;

    /**
     * List all the elements controllable in the started application
     *
     * @throws Exception if an error occurs during the communication with the application
     */
    public void listElements() throws Exception;

}
