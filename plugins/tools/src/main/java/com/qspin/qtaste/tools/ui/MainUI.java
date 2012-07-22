package com.qspin.qtaste.tools.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import org.apache.log4j.Logger;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.qspin.qtaste.tools.action.ImportAction;

public class MainUI extends JFrame {

	public MainUI() {
		super("Converter");
		
		genUI();
		
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public void reloadTree(List<MutableTreeNode> pTreeContent)
	{
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		for ( MutableTreeNode child : pTreeContent ) {
			root.add(child);
		}
		mTree.setModel(new DefaultTreeModel(root));
	}
	
	private void genUI()
	{
		setLayout( new BorderLayout() );
		createAndAddMenuBar();
		
		FormLayout layout = new FormLayout( 
				FRAME_BORDER + ", center:pref" + COMPONENT_SPACING + "pref, " + FRAME_BORDER,
				FRAME_BORDER + ", pref" + COMPONENT_SPACING + "pref, " + FRAME_BORDER);
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		int rowIndex = 2;
		JButton importXml = new JButton("Import recording file");
		importXml.addActionListener(new ImportAction(this));
		builder.add(importXml, cc.xy(2, rowIndex));
		rowIndex += 2;
		
		mTree = new JTree();
		builder.add(new JScrollPane(mTree), cc.xy(2, rowIndex));
		
		add(builder.getPanel(), BorderLayout.CENTER);
	}
	
	private void createAndAddMenuBar() {
		JMenuBar bar = new JMenuBar();
		
		JMenu file = new JMenu("File");
		JMenuItem exit = new JMenuItem("Quit");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent pEvent) {
				System.exit(0);
			}
		});
		file.add(exit);
		bar.add(file);
		
		setJMenuBar(bar);
	}
	
	private JTree mTree;

	private static final String FRAME_BORDER = "3dlu:grow";
	private static final String COMPONENT_SPACING = ", 3dlu,";
	private static final Logger LOGGER = Logger.getLogger(MainUI.class);
	
}
