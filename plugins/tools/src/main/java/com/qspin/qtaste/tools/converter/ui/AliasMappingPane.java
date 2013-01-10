package com.qspin.qtaste.tools.converter.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.qspin.qtaste.tools.converter.ui.model.table.AliasTableModel;

class AliasMappingPane extends JPanel {

	public AliasMappingPane()
	{
		super();
		genUI();
	}
	
	private void genUI()
	{
		setLayout( new BorderLayout() );
		
		mTable = new JTable(new AliasTableModel());
		
		add(new JScrollPane(mTable), BorderLayout.CENTER);
	}
	
	private JTable mTable;
}
