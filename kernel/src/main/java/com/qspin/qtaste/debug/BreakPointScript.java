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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.qspin.qtaste.debug;

import java.awt.Color;
import java.util.HashMap;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

/**
 * @author vdubois
 */
public class BreakPointScript {

    private Document mScriptDocument;
    private String mFileName;
    private HashMap<Integer, Boolean> breakpoints;
    public static String BREAKPOINT_PROPERTY = "breakpoints";
    public static Color BreakpointColor = new Color(252, 157, 159);
    protected BreakpointEventHandler eventBreakpointHandler = BreakpointEventHandler.getInstance();

    public void setFileName(String fileName) {
        mFileName = fileName;
    }

    public BreakPointScript(Document doc) {
        mScriptDocument = doc;
        breakpoints = new HashMap<>();

        mScriptDocument.putProperty(BREAKPOINT_PROPERTY, breakpoints);
        doc.addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {

            }

            public void removeUpdate(DocumentEvent e) {

            }

            public void changedUpdate(DocumentEvent e) {

            }
        });
    }

    public Document getScriptDocument() {
        return mScriptDocument;
    }

    public void setScriptDocument(Document mScriptDocument) {
        this.mScriptDocument = mScriptDocument;
    }

    public void addBreakpoint(int line) {
        getBreakpoints().put(line, true);
        eventBreakpointHandler.addBreakpoint(mFileName, line);

    }

    public void toggleBreakpoint(int line) {
        if (this.getBreakpoints().containsKey(line)) {
            boolean value = getBreakpoints().get(line);
            if (value) {
                removeBreakpoint(line);
            } else {
                addBreakpoint(line);
            }
        } else {
            addBreakpoint(line);
        }
    }

    public void removeBreakpoint(int line) {
        if (getBreakpoints().containsKey(line)) {
            getBreakpoints().put(line, false);
            eventBreakpointHandler.removeBreakpoint(mFileName, line);
        }
    }

    public boolean isBreakPoint(int line) {
        return getBreakpoints().containsKey(line);
    }

    public void removeAllBreakpoints() {
        for (Integer line : getBreakpoints().keySet()) {
            getBreakpoints().put(line, false);
            eventBreakpointHandler.removeBreakpoint(mFileName, line);
        }
        getBreakpoints().clear();
    }

    public HashMap<Integer, Boolean> getBreakpoints() {
        return breakpoints;
    }

}
