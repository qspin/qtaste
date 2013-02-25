package com.qspin.qtaste.addon;

import java.awt.CardLayout;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class ConfigurationTree extends JTree {

	public ConfigurationTree(DefaultMutableTreeNode pRootNode, final JPanel pConfigurationPane)
	{
		super(pRootNode);
		mModel = (DefaultTreeModel)getModel();
		mRoot = (DefaultMutableTreeNode) mModel.getRoot();
		setRootVisible(true);
		getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
                String componentName = e.getNewLeadSelectionPath().getLastPathComponent().toString();
                CardLayout rcl = (CardLayout) pConfigurationPane.getLayout();
                rcl.show(pConfigurationPane, componentName);
            }
        });
	}

	public synchronized void addConfiguration(DefaultMutableTreeNode pConfigNode) {
		mRoot.add(pConfigNode);
		pConfigNode.setParent(mRoot);
		mModel.reload();
	}

	public synchronized void removeConfiguration(DefaultMutableTreeNode pConfigNode) {
		for ( int i = 0; i< mRoot.getChildCount(); ++i)
		{
			if ( ((DefaultMutableTreeNode)mRoot.getChildAt(i)).getUserObject().equals(pConfigNode.getUserObject()))
			{
				mRoot.remove(i);
				break;
			}
		}
		mModel.reload();
	}

	protected DefaultMutableTreeNode mRoot;
	protected DefaultTreeModel mModel;
}
