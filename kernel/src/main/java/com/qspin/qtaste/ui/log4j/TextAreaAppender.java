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

package com.qspin.qtaste.ui.log4j;

/**
 * @author vdubois
 */

import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.SwingUtilities;

import org.apache.log4j.MDC;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Simple example of creating a Log4j appender that will
 * write to a JTextArea.
 */
public class TextAreaAppender extends WriterAppender {

    static private ArrayList<Log4jPanel> jTextAreaList = new ArrayList<>();

    /**
     * Set the target JTextArea for the logging information to appear.
     */
    static public void addTextArea(Log4jPanel jTextArea) {
        jTextAreaList.add(jTextArea);
    }

    static public void removeTextArea(Log4jPanel jTextArea) {
        jTextAreaList.remove(jTextArea);
    }

    @Override
    /**
     * Format and then append the loggingEvent to the stored
     * JTextArea.
     */ public void append(final LoggingEvent loggingEvent) {

        Hashtable mdcContext = MDC.getContext();

        // Append formatted message to textarea using the Swing Thread.
        SwingUtilities.invokeLater(() -> {
            for (Log4jPanel log4jPanel : jTextAreaList) {
                log4jPanel.appendLog(loggingEvent, mdcContext);
            }
        });
    }
}
