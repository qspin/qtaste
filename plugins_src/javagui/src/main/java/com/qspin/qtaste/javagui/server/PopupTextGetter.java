/*
    Copyright 2007-2012 QSpin - www.qspin.be

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

package com.qspin.qtaste.javagui.server;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JScrollPane;

import com.qspin.qtaste.testsuite.QTasteException;

/**
 * Commander wich parses all Popups in order to find the contained message.
 *
 * @author simjan
 */
public class PopupTextGetter extends ComponentCommander {

    /**
     * Commander wich parses all Popups in order to find the contained message.
     *
     * @param data BOOLEAN value : <code>true</code> means that only the text of the active popup has to be returned.
     * <code>false</code> means that all texts have to be returned.
     * @return the list containing the found texts.
     * @throws QTasteException
     */
    @Override
    List<String> executeCommand(int timeout, String componentName, Object... data) throws QTasteException {
        boolean onlyWithFocus = (Boolean) data[0];
        List<String> texts = new ArrayList<>();
        for (JDialog dialog : findPopups()) {
            if (onlyWithFocus && !activateAndFocusComponentWindow(dialog)) {
                // if only the main popup text is needed, ignored popup without focus
                LOGGER.info("the dialog with the title '" + dialog.getTitle() + "' will be ignored");
                continue;
            }

            LOGGER.info("the dialog with the title '" + dialog.getTitle() + "' will not be ignored");

            //find the popup Component
            Object message = getJOptionPane(dialog).getMessage();
            // if message is a scroll pane, use the displayed component
            if (message instanceof JScrollPane)
            {
                message = ((JScrollPane) message).getViewport().getView();
            }
            texts.add(message.toString());
        }
        return texts;
    }

}
