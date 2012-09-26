package com.qspin.qtaste.sutuidemo;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

final class Interface extends JFrame {

	
	public Interface()
	{
		super("SUT GUI Demonstration controlled by QTaste");
		setName("MAIN_FRAME");
		
		genUI();
		
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private void genUI()
	{
		setLayout(new BorderLayout());

		mTabbedPane = new JTabbedPane();
		mTabbedPane.setName("TABBED_PANE");
		mTabbedPane.insertTab(DocumentPanel.COMPONENT_NAME, null, new DocumentPanel(), null, 0);
		mTabbedPane.insertTab(ChoosePanel.COMPONENT_NAME, null, new ChoosePanel(), null, 1);
		mTabbedPane.insertTab(SelectionPanel.COMPONENT_NAME, null, new SelectionPanel(), null, 2);
		mTabbedPane.insertTab(TablePanel.COMPONENT_NAME, null, new TablePanel(), null, 3);
		mTabbedPane.insertTab("UNAMED COMPONENTS", null, new UnamedPanel(), null, 4);
		mTabbedPane.setSelectedIndex(-1);
		
		add(mTabbedPane, BorderLayout.CENTER);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				new Interface();
			}
		});
	}
	
	private JTabbedPane mTabbedPane;

}
