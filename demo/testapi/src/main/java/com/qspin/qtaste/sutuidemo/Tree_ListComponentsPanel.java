package com.qspin.qtaste.sutuidemo;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTree;

public class Tree_ListComponentsPanel extends JPanel {

	public Tree_ListComponentsPanel()
	{
		super();
		setName(COMPONENT_NAME);
		
		genUI();
	}
	
	private void genUI()
	{
		setLayout(new GridLayout(1, 2));
		prepareComponents();
		add(mTree);
		add(mTree2);
	}
	
	private void prepareComponents()
	{
		mTree.setName("TREE");
		mTree.setBorder(BorderFactory.createTitledBorder("JTree"));
		mTree.setCellRenderer(new CustomTreeCellRenderer());
		mTree2.setName("TREE_2");
		mTree2.setRootVisible(false);
		mTree2.setBorder(BorderFactory.createTitledBorder("JTree 2"));
		mTree2.setCellRenderer(new CustomTreeCellRenderer());
	}

	private JTree mTree = new JTree(ModelBuilder.getTreeModel());
	private JTree mTree2 = new JTree(ModelBuilder.getTreeModel());
	
	public static final String COMPONENT_NAME = "TREE_LIST_PANEL";
}
