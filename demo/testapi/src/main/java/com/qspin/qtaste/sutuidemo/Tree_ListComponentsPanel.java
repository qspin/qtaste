package com.qspin.qtaste.sutuidemo;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTree;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class Tree_ListComponentsPanel extends JPanel {

	public Tree_ListComponentsPanel()
	{
		super();
		setName(COMPONENT_NAME);
		
		genUI();
	}
	
	private void genUI()
	{
		prepareComponents();

		FormLayout layout = new FormLayout("3dlu, fill:pref:grow, 3dlu, fill:pref:grow, 3dlu", 
										   "3dlu, fill:pref:grow, 3dlu");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		
		builder.add(mTree, cc.xy(2,2));
		builder.add(mTree2, cc.xy(4,2));

		setLayout(new BorderLayout());
		add(builder.getPanel(), BorderLayout.CENTER);
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
