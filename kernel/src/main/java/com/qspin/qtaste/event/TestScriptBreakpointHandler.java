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

import org.apache.log4j.Logger;

import com.qspin.qtaste.debug.Breakpoint;
import com.qspin.qtaste.util.Log4jLoggerFactory;

/**
 *
 * @author vdubois
 */
public class TestScriptBreakpointHandler {
    private static Logger logger = Log4jLoggerFactory.getLogger(TestScriptBreakpointHandler.class);
    private static TestScriptBreakpointHandler eventHandler;
    private final List<TestScriptBreakpointListener> testbreakPointListeners = 
            Collections.synchronizedList(new LinkedList<TestScriptBreakpointListener>());

    private TestScriptBreakpointHandler(){}

    public static TestScriptBreakpointHandler getInstance(){
        if (eventHandler == null) {
            eventHandler = new TestScriptBreakpointHandler();
        }
        return eventHandler;
    }

    public void clearInstance(){
        eventHandler = null;
    }

    
    public void addTestScriptBreakpointListener(TestScriptBreakpointListener tcl){
        if (tcl != null && !testbreakPointListeners.contains(tcl)){
            testbreakPointListeners.add(tcl);
        }
    }

    public List<TestScriptBreakpointListener> getTestCaseListeners(){
        return testbreakPointListeners;
    }

    public void removeTestScriptBreakpointListener(TestScriptBreakpointListener tcl){
    	if (tcl==null) return;
        testbreakPointListeners.remove(tcl);
    }

    public void break_(String fileName, int lineNumber){
        TestScriptBreakpointEvent event = new TestScriptBreakpointEvent(this , TestScriptBreakpointEvent.Action.BREAK, new Breakpoint(fileName, lineNumber));
        doAction(event);
        logger.debug("DoAction TestScriptBreakpointEvent.Action.BREAK at " + fileName + ":" + lineNumber);
    }

    public void continue_(){
        TestScriptBreakpointEvent event = new TestScriptBreakpointEvent(this, TestScriptBreakpointEvent.Action.CONTINUE, null);
        doAction(event);
        logger.debug("DoAction TestScriptBreakpointEvent.Action.CONTINUE");
    }
    
    public void step(){
        TestScriptBreakpointEvent event = new TestScriptBreakpointEvent(this, TestScriptBreakpointEvent.Action.STEP, null);
        doAction(event);
        logger.debug("DoAction TestScriptBreakpointEvent.Action.STEP");
    }

    public void stepInto(){
        TestScriptBreakpointEvent event = new TestScriptBreakpointEvent(this, TestScriptBreakpointEvent.Action.STEPINTO, null);
        doAction(event);
        logger.debug("DoAction TestScriptBreakpointEvent.Action.STEPINTO");
    }
    
    public void stop(){
        TestScriptBreakpointEvent event = new TestScriptBreakpointEvent(this , TestScriptBreakpointEvent.Action.STOP, null);
        doAction(event);
        logger.debug("DoAction TestScriptBreakpointEvent.Action.STOP");
    }

    public void dumpStack(){
        TestScriptBreakpointEvent event = new TestScriptBreakpointEvent(this, TestScriptBreakpointEvent.Action.DUMP_STACK, null);
        doAction(event);
        logger.debug("DoAction TestScriptBreakpointEvent.Action.DUMP_STACK");
    }
    
    /**
     * This method is invoked by the UI to dump the variable content
     */
    public void dumpVar(Object varName){
        TestScriptBreakpointEvent event = new TestScriptBreakpointEvent(this, TestScriptBreakpointEvent.Action.DUMP_VAR, varName);
        doAction(event);
    }
    
	private void doAction(TestScriptBreakpointEvent event){    	
        synchronized(testbreakPointListeners){
            Iterator<TestScriptBreakpointListener> it = testbreakPointListeners.iterator();
            TestScriptBreakpointListener tcl;
            while (it.hasNext()) {
                try {
                    tcl = it.next();                    
                    logger.debug("TestScriptBreakpointHandler: send event " + event.getAction().toString() + " to " + tcl.getClass().getName());
                    tcl.doAction(event);
                }
                catch (Exception e) {
                    logger.error(e.getMessage());
                    e.printStackTrace();
                    //return;
                }
            }
        }
    }

}
