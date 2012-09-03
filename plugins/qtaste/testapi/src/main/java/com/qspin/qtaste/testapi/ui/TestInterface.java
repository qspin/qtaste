package com.qspin.qtaste.testapi.ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

final class TestInterface extends JFrame {

	
	public TestInterface()
	{
		super("Test Interface");
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
		mTabbedPane.insertTab(TestDocumentPanel.COMPONENT_NAME, null, new TestDocumentPanel(), null, 0);
		mTabbedPane.insertTab(TestChoosePanel.COMPONENT_NAME, null, new TestChoosePanel(), null, 1);
		mTabbedPane.insertTab(TestSelectionPanel.COMPONENT_NAME, null, new TestSelectionPanel(), null, 2);
		mTabbedPane.insertTab(TestTablePanel.COMPONENT_NAME, null, new TestTablePanel(), null, 3);
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
				new TestInterface();
			}
		});
	}
	
	private JTabbedPane mTabbedPane;

}
