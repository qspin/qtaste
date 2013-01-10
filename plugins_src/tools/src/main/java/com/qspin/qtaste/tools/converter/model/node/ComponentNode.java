package com.qspin.qtaste.tools.converter.model.node;

import javax.swing.tree.DefaultMutableTreeNode;

public class ComponentNode extends DefaultMutableTreeNode {

	public ComponentNode(String pComponentName, String pComponentClass)
	{
		super(pComponentName);
		mComponentClass = pComponentClass;
	}
	
	public String getComponentClass()
	{
		return mComponentClass;
	}
	
	private String mComponentClass;
}
