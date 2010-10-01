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

import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testapi.api.Utility;
import javax.swing.JOptionPane;
import com.thoughtworks.xstream.XStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.ObjectInputStream;
import javax.swing.JDialog;

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

    public void showMessageDialog(String title, String message, boolean modal) {
        if (messageDialog == null) {
            optionPane = new JOptionPane(message, JOptionPane.PLAIN_MESSAGE);
            messageDialog = optionPane.createDialog(title);
            messageDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        } else {
            messageDialog.setTitle(title);
            optionPane.setMessage(message);
        }
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

    public Object loadXStreamFile(String xStreamFileName) throws QTasteException
    {
        BufferedReader br = null;
        {
            ObjectInputStream inputStream = null;
            try {
                File xStreamFile = new File(xStreamFileName);
                XStream xstream = new XStream();
                br = new BufferedReader(new FileReader(xStreamFile));
                inputStream = xstream.createObjectInputStream(br);
                Object dataObject = inputStream.readObject();
                inputStream.close();
                br.close();
                return dataObject;
            } catch (Exception ex) {
                throw new QTasteException(ex.getMessage());
            }
        }
    }

}
