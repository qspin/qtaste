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

import java.awt.Component;
import java.awt.Container;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import javafx.application.Platform;

import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;

/**
 * Commander which sets a value in the input field of a popup.
 *
 * @see JOptionPane#showInputDialog(Object)
 */
public class PopupTextSetter extends UpdateComponentCommander {

    /**
     * Commander which sets a value in the input field of a popup.
     *
     * @param data INTEGER - the timeout value; OBJECT - with the value to insert. The toString method will be used on the object.
     * @return true if the command is successfully performed.
     * @throws QTasteException
     */
    @Override
    public Boolean executeCommand(int timeout, String componentName, Object... data) throws QTasteException {
        setData(data);
        long maxTime = System.currentTimeMillis() + 1000 * timeout;

        while (System.currentTimeMillis() < maxTime) {
            JDialog targetPopup = null;
            for (JDialog dialog : findPopups()) {
                if (!dialog.isVisible() || !dialog.isEnabled()) {
                    String msg = "Ignore the dialog '" + dialog.getTitle() + "' cause:\n ";
                    if (!dialog.isVisible()) {
                        msg += "\t is not visible";
                    }
                    if (!dialog.isEnabled()) {
                        msg += "\t is not enabled";
                    }
                    LOGGER.info(msg);
                    continue;
                }
                //				if (activateAndFocusComponentWindow(dialog))
                //				{
                //					targetPopup = dialog;
                //				}
                //				else
                //				{
                //					LOGGER.info("Ignore the dialog '" + dialog.getTitle() + "' cause:\n  \t is not focused");
                //				}
            }
            //			component = findTextComponent(targetPopup);
            //
            //			if ( component != null && !component.isDisabled() && checkComponentIsVisible(component) )
            //				break;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOGGER.warn("Exception during the component search sleep...");
            }
        }

        if (component == null) {
            throw new QTasteTestFailException("The text field component is not found.");
        }
        if (component.isDisabled()) {
            throw new QTasteTestFailException("The text field component is not enabled.");
        }
        if (!checkComponentIsVisible(component)) {
            throw new QTasteTestFailException("The text field component is not visible!");
        }

        prepareActions();
        Platform.runLater(this);

        return true;
    }

    private JTextField findTextComponent(Component c) {
        if (c instanceof JTextField) {
            return (JTextField) c;
        } else if (c instanceof Container) {
            for (Component comp : ((Container) c).getComponents()) {
                JTextField jtf = findTextComponent(comp);
                if (jtf != null) {
                    return jtf;
                }
            }
        }
        return null;
    }

    @Override
    protected void prepareActions() throws QTasteTestFailException {
        //Do nothing
    }

    @Override
    protected void doActionsInEventThread() throws QTasteTestFailException {
        //		((JTextField)component).setText(mData[0].toString());
    }
}
