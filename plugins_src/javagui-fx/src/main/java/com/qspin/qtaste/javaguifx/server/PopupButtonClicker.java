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

package com.qspin.qtaste.javaguifx.server;

import java.util.Collections;
import java.util.List;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;

import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

/**
 * Commander which clicks on a popup button.
 */
public class PopupButtonClicker extends ButtonClicker {

    /**
     * Commander which clicks on a popup button.
     *
     * @param data INTEGER - the timeout value; String - the button text.
     * @return true if the command is successfully performed.
     * @throws QTasteException
     */
    @Override
    public Boolean executeCommand(int timeout, String componentName, Object... data) throws QTasteException {
        setData(data);
        long maxTime = System.currentTimeMillis() + 1000 * timeout;
        String buttonText = mData[0].toString();
        component = null;

        while (System.currentTimeMillis() < maxTime) {
            DialogPane targetPopup = null;
            List<Stage> popups = findPopups();
            Collections.reverse(popups);
            for (Stage popup : popups) {
                DialogPane dialogPane = getDialogPane(popup);
                if (!dialogPane.isVisible() || dialogPane.isDisabled()) {
                    String msg = "Ignore the dialog '" + popup.getTitle() + "' cause:\n ";
                    if (!dialogPane.isVisible()) {
                        msg += "\t is not visible";
                    }
                    if (dialogPane.isDisabled()) {
                        msg += "\t is disabled";
                    }
                    LOGGER.info(msg);
                    continue;
                }
                if (activateAndFocusWindow(popup)) {
                    targetPopup = dialogPane;
                    break;
                } else {
                    LOGGER.info("Ignore the dialog '" + popup.getTitle() + "' cause:\n  \t is not focused");
                }
            }

            component = findButton(targetPopup, buttonText);

            if ( component != null && !component.isDisabled() && checkComponentIsVisible(component) )
                break;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOGGER.warn("Exception during the component search sleep...");
            }
        }

        if (component == null) {
            throw new QTasteTestFailException("The button with the text \"" + buttonText + "\" is not found.");
        }
        if (component.isDisabled()) {
            throw new QTasteTestFailException("The button with the text \"" + buttonText + "\" is disabled.");
        }
        if (!checkComponentIsVisible(component)) {
            throw new QTasteTestFailException("The button with the text \"" + buttonText + "\" is not visible!");
        }

        prepareActions();
        Platform.runLater(this);
        return true;
    }

    private ButtonBase findButton(Node node, String buttonText) {
        if ((node instanceof ButtonBase) && (((ButtonBase) node).getText().equals(buttonText))) {
            return (ButtonBase) node;
        } else if (node instanceof Parent) {
            for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
                ButtonBase button = findButton(child, buttonText);
                if (button != null) {
                    return button;
                }
            }
        }
        return null;
    }
}
