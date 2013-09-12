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
 * Utility.java
 */
package com.qspin.qtaste.testapi.api;

import com.qspin.qtaste.kernel.testapi.SingletonComponent;
import com.qspin.qtaste.testsuite.QTasteException;


/**
 * Utility is the interface of the QTaste Test API component providing some utility verbs,
 * for example to show message dialogs and load XStream files.
 */
public interface Utility extends SingletonComponent {

    /**
     * Create a capture of the screen and save the content as PNG in the specified location.
     *
     * @param fileName full path to the generated png file
     * @throws QTasteException if the screenshot cannot be performed sucessfully
     */
    public void createScreenshot(String fileName) throws QTasteException;

    /**
     * Shows a message dialog window displaying information to the tester, modal or not (modeless) as specified.
     * If the dialog is modal, the test is suspended until the dialog window is closed.
     *
     * @param title the title of the dialog window
     * @param message the message to be displayed in the dialog
     * @param modal true if the dialog window must be modal, false otherwise
     */
    public void showMessageDialog(String title, String message, boolean modal);

    /**
     * Hides the modeless message window displayed using {@link #showMessageDialog}.
     */
    public void hideMessageDialog();

    public String getUserStringValue(String messageToDisplay, Object defaultValue) throws QTasteException;
}
