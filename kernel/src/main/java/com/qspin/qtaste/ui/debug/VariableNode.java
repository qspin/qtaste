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

@SuppressWarnings("serial")
public class VariableNode extends DebugNode {

	private DebugVariable mVariable;
	Object[] children; 
	
	public VariableNode(DebugVariable variable) {
		mVariable = variable;
	}
	public DebugVariable getVariable() {
		return mVariable;
	}
    public String toString() { 
    	if (mVariable==null) return "";
    	return mVariable.getVarName();
        }

    public boolean hasChildren(){
    	if (mVariable==null) return true;
    	return mVariable.getFieldList().size()>0;
    }

    /**
     * Loads the children, caching the results in the children ivar.
     */
    protected Object[] getChildren() {
		if (children != null) {
		    return children; 
		}
			
		ArrayList<DebugVariable> fields =mVariable.getFieldList();
		if (fields!=null) {
			children = new VariableNode[fields.size()];
			for (int i=0; i < fields.size(); i++) {
				DebugVariable variable = fields.get(i);
				children[i] = new VariableNode(variable);
			}
			return children;
		}
		return null;
    }
    
}
