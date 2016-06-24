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
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.mutable.MutableBoolean;

import com.qspin.qtaste.testapi.api.Utility;
import com.qspin.qtaste.testsuite.QTasteException;

/**
 * @author Vincent Dubois
 */
public class UtilityImpl implements Utility {

    public UtilityImpl() throws Exception {
        initialize();
    }

    public void initialize() throws QTasteException {
        // nothing to do
    }

    public void terminate() throws QTasteException {
        // nothing to do
    }

    public void createScreenshot(String fileName) throws QTasteException {
        try {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Rectangle screenRectangle = new Rectangle(screenSize);
            Robot robot = new Robot();
            BufferedImage image = robot.createScreenCapture(screenRectangle);
            ImageIO.write(image, "png", new File(fileName));
        } catch (Exception e) {
            throw new QTasteException("Error in createScreenshot: " + e.getMessage());
        }
    }

    public void showMessageDialog(final String title, final String message) throws QTasteException {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(null, message, title, JOptionPane.PLAIN_MESSAGE);
                }
            });
        } catch (Exception e) {
            throw new QTasteException("Error while showing message dialog", e);
        }
    }

    @Override
    public String getUserStringValue(final String message, final Object defaultValue) throws QTasteException {
        final StringBuilder valueBuilder = new StringBuilder();
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    String value = JOptionPane.showInputDialog(message, defaultValue);
                    valueBuilder.append(value);
                }
            });
        } catch (Exception e) {
            throw new QTasteException("Error while showing user input dialog", e);
        }
        return valueBuilder.toString();
    }

    @Override
    public boolean getUserConfirmation(final String title, final String message) throws QTasteException {
        final MutableBoolean confirmed = new MutableBoolean();
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    int result = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
                    confirmed.setValue(result == JOptionPane.YES_OPTION);
                }
            });
        } catch (Exception e) {
            throw new QTasteException("Error while showing user confirmation dialog", e);
        }
        return confirmed.booleanValue();
    }
}
