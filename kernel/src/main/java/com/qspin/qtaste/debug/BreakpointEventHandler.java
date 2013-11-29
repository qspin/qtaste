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

*/
package com.qspin.qtaste.debug;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class BreakpointEventHandler{


    private static BreakpointEventHandler eventHandler;
    private final List<BreakpointListener> breakpointListeners = Collections.synchronizedList(new LinkedList<BreakpointListener>());
    private final List<Breakpoint> breakpoints = Collections.synchronizedList(new LinkedList<Breakpoint>());

    private BreakpointEventHandler(){}

    public static BreakpointEventHandler getInstance(){
        if (eventHandler == null) {
            eventHandler = new BreakpointEventHandler();
        }
        return eventHandler;
    }

    public void clearInstance(){
        eventHandler = null;
    }

    
    public void addBreakpointListener(BreakpointListener tcl){
        if (tcl != null && !breakpointListeners.contains(tcl)){
            breakpointListeners.add(tcl);
        }
    }

    public List<BreakpointListener> getBreakpointListeners(){
        return breakpointListeners;
    }

    public void removeBreakpointListener(BreakpointListener tcl){
        breakpointListeners.remove(tcl);
    }

    public void addBreakpoint(String fileName, int lineIndex){
        BreakpoinkEvent tce = new BreakpoinkEvent(lineIndex, true);
        synchronized(breakpointListeners){
            breakpoints.add(new Breakpoint(fileName, lineIndex));
            
            Iterator<BreakpointListener> it = breakpointListeners.iterator();
            BreakpointListener tcl;
            while (it.hasNext()) {
                tcl = it.next();
                tcl.addBreakpoint(tce);
            }
        }
    }

    private Breakpoint getBreakPoint(String fileName, int lineIndex){
        Iterator<Breakpoint> breakpointIterator = breakpoints.iterator();
        while (breakpointIterator.hasNext())
        {
            Breakpoint breakPoint = breakpointIterator.next();
            if (breakPoint.getFileName().equals(fileName) &&
                breakPoint.getLineIndex()==lineIndex)
            {
                return breakPoint;
            }
        }
        return null;
    }
    public void removeBreakpoint(String fileName, int lineIndex){
        BreakpoinkEvent tce = new BreakpoinkEvent(lineIndex, false);
        synchronized(breakpointListeners){
            Breakpoint breakpoint = this.getBreakPoint(fileName, lineIndex);
            if (breakpoint!=null) {
                breakpoints.remove(breakpoint);
            }
            Iterator<BreakpointListener> it = breakpointListeners.iterator();
            BreakpointListener tcl;
            while (it.hasNext()) {
                tcl = it.next();
                tcl.addBreakpoint(tce);
            }
        }
    }
    
    public List<Breakpoint> getBreakpoints() {
        return breakpoints;
    }
}
