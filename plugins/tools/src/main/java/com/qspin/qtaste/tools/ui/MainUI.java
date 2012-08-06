package com.qspin.qtaste.tools.ui;

import static com.qspin.qtaste.tools.ui.UIConstants.COMPONENT_SPACING;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.qspin.qtaste.tools.action.ConversionTask;
import com.qspin.qtaste.tools.model.EventManager;
import com.qspin.qtaste.tools.ui.action.ImportAction;

public class MainUI extends JFrame {

	public MainUI() {
		super("Converter");
		
		genUI();
		
		setMinimumSize(new Dimension(800,600));
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	private void genUI()
	{
		setLayout( new BorderLayout() );
		createAndAddMenuBar();
		
		FormLayout layout = new FormLayout( 
				FRAME_BORDER + ", pref:grow, " + FRAME_BORDER,
				FRAME_BORDER + ", pref" + COMPONENT_SPACING + "pref" + COMPONENT_SPACING + "pref" + COMPONENT_SPACING + "pref, " + FRAME_BORDER);
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		int rowIndex = 2;
		JButton importXml = new JButton("Import recording file");
		importXml.addActionListener(new ImportAction(this));
		builder.add(importXml, cc.xy(2, rowIndex));
		rowIndex += 2;
		
		JTabbedPane tabbedPanes = new JTabbedPane();
		tabbedPanes.insertTab("Description", null, new EventDescriptionPane(), null, 0);
		tabbedPanes.insertTab("Aliases", null, new AliasMappingPane(), null, 1);
		builder.add(tabbedPanes, cc.xy(2,rowIndex));
		rowIndex += 2;
		
		mConfigurationPane = new ConversionConfigurationPane();
		EventManager.getInstance().addPropertyChangeListener(mConfigurationPane);
		builder.add(mConfigurationPane, cc.xy(2, rowIndex));
		rowIndex += 2;
		
		JPanel p = new JPanel();
		JButton launch = new JButton("Launch conversion");
		launch.addActionListener(new LauchConversionAction());
		p.add(launch);
		builder.add(p, cc.xy(2, rowIndex));
		rowIndex += 2;
		
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
	
	private class LauchConversionAction implements ActionListener
	{
		public void actionPerformed(ActionEvent pEvt)
		{
			try {
				ConversionTask task = new ConversionTask();
				task.setAcceptedComponentName(mConfigurationPane.getSelectedComponent());
				task.setAcceptedEventType(mConfigurationPane.getSelectedEventType());
				task.setOutputDirectory(mConfigurationPane.getOutputDirectory());
				new Thread(task).start();
			}
			catch (IOException pExc)
			{
				LOGGER.error(pExc);
				JOptionPane.showConfirmDialog(MainUI.this, pExc.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private ConversionConfigurationPane mConfigurationPane;
	
	private static final String FRAME_BORDER = "3dlu";
	private static final Logger LOGGER = Logger.getLogger(MainUI.class);
	
}
