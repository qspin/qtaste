package com.qspin.qtaste.tools.ui;

import static com.qspin.qtaste.tools.ui.UIConstants.COMPONENT_SPACING;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import org.apache.log4j.Logger;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.qspin.qtaste.tools.action.ConversionTask;
import com.qspin.qtaste.tools.factory.ComponentTreeFactory;
import com.qspin.qtaste.tools.factory.EventTreeFactory;
import com.qspin.qtaste.tools.model.EventManager;
import com.qspin.qtaste.tools.model.event.Event;
import com.qspin.qtaste.tools.ui.action.FactorySelection;
import com.qspin.qtaste.tools.ui.action.ImportAction;
import com.qspin.qtaste.tools.ui.event.EventPane;

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
	
	public void reloadTree(List<MutableTreeNode> pTreeContent)
	{
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		for ( MutableTreeNode child : pTreeContent ) {
			root.add(child);
		}
		mTree.setModel(new DefaultTreeModel(root));
	}
	
	public void setEvents(List<Event> pEvents)
	{
		if ( pEvents == null )
		{
			pEvents = new ArrayList<Event>();
		}
		mEvents = pEvents;
		mTree.setTreeData(mEvents);
	}
	
	private void genUI()
	{
		setLayout( new BorderLayout() );
		createAndAddMenuBar();
		
		FormLayout layout = new FormLayout( 
				FRAME_BORDER + ", right:pref:grow" + COMPONENT_SPACING + "pref" + COMPONENT_SPACING + "pref" + COMPONENT_SPACING + "pref:grow, " + FRAME_BORDER,
				FRAME_BORDER + ", pref" + COMPONENT_SPACING + "pref" + COMPONENT_SPACING + "pref" + COMPONENT_SPACING + "pref" + COMPONENT_SPACING + "pref, " + FRAME_BORDER);
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		int rowIndex = 2;
		JButton importXml = new JButton("Import recording file");
		importXml.addActionListener(new ImportAction(this));
		builder.add(importXml, cc.xyw(2, rowIndex,5));
		rowIndex += 2;
		
		builder.addLabel("Group by :", cc.xy(2, rowIndex));
		ButtonGroup group = new ButtonGroup();
		JRadioButton groupByComponent = new JRadioButton("component(s)", false);
		groupByComponent.addItemListener(new FactorySelection(mTree, ComponentTreeFactory.getInstance()));
		group.add(groupByComponent);
		builder.add(groupByComponent, cc.xy(4, rowIndex));
		JRadioButton groupByEvent = new JRadioButton("event(s)", false);
		groupByEvent.addItemListener(new FactorySelection(mTree, EventTreeFactory.getInstance()));
		group.add(groupByEvent);
		builder.add(groupByEvent, cc.xy(6, rowIndex));
		groupByComponent.setSelected(true);
		
		EventPane eventPane = new EventPane();
		mTree.addTreeSelectionListener(eventPane);
		builder.add(eventPane, cc.xywh(8, rowIndex-2, 1, 5));
		rowIndex += 2;
		
		builder.add(new JScrollPane(mTree), cc.xyw(2, rowIndex, 5));
		rowIndex += 2;
		
		mConfigurationPane = new ConversionConfigurationPane();
		EventManager.getInstance().addPropertyChangeListener(mConfigurationPane);
		builder.add(mConfigurationPane, cc.xyw(2, rowIndex, 7));
		rowIndex += 2;
		
		JPanel p = new JPanel();
		JButton launch = new JButton("Launch conversion");
		launch.addActionListener(new LauchConversionAction());
		p.add(launch);
		builder.add(p, cc.xyw(2, rowIndex, 7));
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

	private EventTree mTree = new EventTree();
	private ConversionConfigurationPane mConfigurationPane;
	private List<Event> mEvents;
	
	private static final String FRAME_BORDER = "3dlu";
	private static final Logger LOGGER = Logger.getLogger(MainUI.class);
	
}
