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

package com.qspin.qtaste.event;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author vdubois
 */
public class DumpPythonResultEventHandler {
    private static DumpPythonResultEventHandler eventHandler;
    private final List<DumpPythonResultListener> dumpPythonResultListeners = Collections.synchronizedList(new LinkedList<DumpPythonResultListener>());

    private DumpPythonResultEventHandler(){}

    public static DumpPythonResultEventHandler getInstance(){
        if (eventHandler == null) {
            eventHandler = new DumpPythonResultEventHandler();
        }
        return eventHandler;
    }

    public void clearInstance(){
        eventHandler = null;
    }

    
    public void addPythonResultListener(DumpPythonResultListener tcl){
        if (tcl != null && !dumpPythonResultListeners.contains(tcl)){
            dumpPythonResultListeners.add(tcl);
        }
    }

    public List<DumpPythonResultListener> getPythonResultListeners(){
        return dumpPythonResultListeners;
    }

    public void removePythonResultListener(DumpPythonResultListener tcl){
        dumpPythonResultListeners.remove(tcl);
    }

    public void pythonResult(Object result){
        DumpPythonResultEvent tce = new DumpPythonResultEvent(result);
        synchronized(dumpPythonResultListeners){
            Iterator<DumpPythonResultListener> it = dumpPythonResultListeners.iterator();
            DumpPythonResultListener tcl;
            while (it.hasNext()) {
                tcl = it.next();
                tcl.pythonResult(tce);
            }
        }
    }
}
