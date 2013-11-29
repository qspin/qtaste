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

package com.qspin.qtaste.ui.debug;

import java.util.ArrayList;
import java.util.Iterator;

@SuppressWarnings("serial")
public class DebugRootNode extends DebugNode 
{

	private ArrayList<DebugVariable> mDebugVariables;
	Object[] children; 
	
	public DebugRootNode() {
		
	}

	public void setDebugVariables(ArrayList<DebugVariable> debugVariables) {
		mDebugVariables = debugVariables;
	}
    public String toString() { 
    	return "Variables";
        }

    public boolean hasChildren(){
    	if (mDebugVariables!=null)
    		return mDebugVariables.size()>0;
    		else return false;
    }
    	


    /**
     * Loads the children, caching the results in the children ivar.
     */
    protected Object[] getChildren() {
    	if (mDebugVariables==null) return null;
		if (children != null) {
		    return children; 
		}
		Object[] returnc = new Object[mDebugVariables.size()];
		int i=0;
		Iterator<DebugVariable> debugVariables = mDebugVariables.iterator();
		while (debugVariables.hasNext()) {
			DebugVariable debugVariable =debugVariables.next();
			VariableNode variableNode = new VariableNode(debugVariable);
			returnc[i] = variableNode;
			i++;
		}
		children = returnc;
		return returnc;
    }
	
}
