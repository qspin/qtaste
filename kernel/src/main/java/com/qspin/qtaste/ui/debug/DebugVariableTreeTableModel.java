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

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import com.qspin.qtaste.ui.treetable.TreeTableModel;

@SuppressWarnings("serial")
public class DebugVariableTreeTableModel extends DefaultTreeModel //AbstractTreeTableModel 
			implements TreeTableModel {

    ArrayList<String> variableList;

    // Types of the columns.
    static protected Class<?>[]  cTypes = {TreeTableModel.class, String.class};
    
    public void setDebugVariables(ArrayList<DebugVariable> debugVariables) {
		DebugRootNode rootNode = (DebugRootNode)this.getRoot();
		// remove all children
        ArrayList<TreeNode> childNodes = new ArrayList<TreeNode>();
        if (rootNode.getChildren()!=null) {
	        int[] indices = new int[rootNode.getChildCount()];
	        int index=0;
			for (Object childNode :rootNode.getChildren()) {
	            indices[index]=index;
				VariableNode childVarNode = (VariableNode)childNode;
				childNodes.add(childVarNode);
				index++;
			}
			this.fireTreeNodesRemoved(this, this.getPathToRoot(rootNode), indices, childNodes.toArray());
			rootNode.children =null;
			rootNode.removeAllChildren();
        }
		rootNode.setDebugVariables(debugVariables);
		int currentIndex=0;
		for (Object childNode :rootNode.getChildren()) {
			rootNode.add((VariableNode)childNode);
 	        this.fireTreeNodesInserted(this, new Object [] {this.getRoot()}, 
	        		new int[] {currentIndex}, 
	        		new Object[] {childNode});
 	        ///
 	       insertNode(childNode);
 	        //
			currentIndex++;
		}
        this.nodeChanged((TreeNode)this.getRoot());
    }
    
    private void insertNode(Object childNode) {
		int currentIndex2=0;
		for (Object fieldNode : ((VariableNode)childNode).getChildren()) {
			((VariableNode)childNode).add((VariableNode)fieldNode);				
	        this.fireTreeNodesInserted(this, new Object [] {childNode}, 
	        		new int[] {currentIndex2},new Object[] {fieldNode}); 
			currentIndex2++;
			insertNode(fieldNode);
		}
    	
    }
    
    public DebugVariableTreeTableModel() {
		super(new DebugRootNode());
	}

	//private static Logger logger = Log4jLoggerFactory.getLogger(TestCampaignTreeModel.class);
	
	public Class<?> getColumnClass(int column) {
        if (column==0) return TreeTableModel.class;
        else return String.class;
	}

	public int getColumnCount() {
		return 3;
	}

	public String getColumnName(int column) {
        if (column==0) return "Variable";
        if (column==1) return "Type";
        if (column==2) return "Value";
        return "";
    }

	public Object getValueAt(Object node, int column) {
		if (node instanceof VariableNode) {
			VariableNode varNode = (VariableNode)node;
			DebugVariable debugVariable = varNode.getVariable();
			if (debugVariable==null) return "";
			
			if (column==0) return debugVariable.getVarName();
			if (column==1) return debugVariable.getType();
			if (column==2) return debugVariable.getValue();
		}
		if (node instanceof DebugRootNode) {
			if (column==0) return "Variables";
			if (column==1) return "";
			if (column==2) return "";
		}
		return "";
	}

	public boolean isCellEditable(Object node, int column) {
		return true;		
	}

	public void setValueAt(Object value, Object node, int column) {
		//TBD
		// nothing to do
	}

    protected DebugNode getVariable(Object node) {
    	DebugNode debugNode = ((DebugNode)node); 
    	return debugNode;       
        }
    
    // The superclass's implementation would work, but this is more efficient. 
    public boolean isLeaf(Object node) {
    	return !getVariable(node).hasChildren();
    }	
	
}
