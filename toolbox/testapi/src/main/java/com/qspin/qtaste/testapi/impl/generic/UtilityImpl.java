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

package com.qspin.qtaste.testapi.impl.generic;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.qspin.qtaste.testapi.api.Utility;
import com.qspin.qtaste.testsuite.QTasteException;

/**
 *
 * @author Vincent Dubois
 */
public class UtilityImpl implements Utility {

    private JOptionPane optionPane;
    private JDialog messageDialog;

    public UtilityImpl() throws Exception {
        initialize();
    }

    public void initialize() throws QTasteException {
    }

    public void terminate() throws QTasteException {
        if (messageDialog != null) {
            optionPane = null;
            messageDialog.setVisible(false);
            messageDialog.dispose();
            messageDialog = null;
        }
    }

    public void createScreenshot(String fileName) throws QTasteException {
		try {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Rectangle screenRectangle = new Rectangle(screenSize);
			Robot robot = new Robot();
			BufferedImage image = robot.createScreenCapture(screenRectangle);
			ImageIO.write(image, "png", new File(fileName));
		}
		catch (Exception e) {
			throw new QTasteException("Error in createScreenshot: " + e.getMessage());
		}
	}

    public void showMessageDialog(String title, String message, boolean modal) {

    	optionPane = new JOptionPane(message, JOptionPane.PLAIN_MESSAGE);
        messageDialog = optionPane.createDialog(title);
        messageDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);

        // don't set modal to true now, because we use the setAlwaysOnTop(true)/setAlwaysOnTop(false)
        // trick to get the focus, but we don't really want an "AlwaysOnTop" window
        messageDialog.setModal(false);
        messageDialog.pack();
        messageDialog.setAlwaysOnTop(true);
        messageDialog.setVisible(true);
        messageDialog.setAlwaysOnTop(false);

        if (modal) {
            messageDialog.setVisible(false);
            messageDialog.setModal(true);
            messageDialog.setVisible(true);
        }
    }

    public void hideMessageDialog() {
        messageDialog.setVisible(false);
    }

	@Override
	public String getUserStringValue(String messageToDisplay, Object defaultValue) throws QTasteException {
		return JOptionPane.showInputDialog(messageToDisplay, defaultValue);
	}

	@Override
	public boolean getUserConfirmation(String messageToDisplay) throws QTasteException {

		optionPane = new JOptionPane(messageToDisplay, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
        messageDialog = optionPane.createDialog("Input");
        messageDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);

        // don't set modal to true now, because we use the setAlwaysOnTop(true)/setAlwaysOnTop(false)
        // trick to get the focus, but we don't really want an "AlwaysOnTop" window
        messageDialog.setModal(false);
        messageDialog.pack();
        messageDialog.setAlwaysOnTop(true);
        messageDialog.setVisible(true);
        messageDialog.setAlwaysOnTop(false);

        messageDialog.setVisible(false);
        messageDialog.setModal(true);
        messageDialog.setVisible(true);

        // Get selected option value
        Object selectedValue = optionPane.getValue();
        if(selectedValue == null) {
        	return false;
        }
        if(selectedValue instanceof Integer) {
        	return ( ((Integer)selectedValue).intValue() == JOptionPane.YES_OPTION);
        }

        return false;
	}

}
