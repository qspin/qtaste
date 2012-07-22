package com.qspin.qtaste.tools.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.qspin.qtaste.tools.factory.EventNodeFactory;
import com.qspin.qtaste.tools.io.XMLEventHandler;
import com.qspin.qtaste.tools.model.Event;
import com.qspin.qtaste.tools.ui.MainUI;

public class ImportAction implements ActionListener {

	public ImportAction(MainUI pCaller) {
		mCaller = pCaller;
	}

	public void actionPerformed(ActionEvent pEvent) {
		JFileChooser jfc = new JFileChooser();
		if (jfc.showOpenDialog(mCaller) == JFileChooser.APPROVE_OPTION) {
			processFile(jfc.getSelectedFile());

		}
	}

	private void processFile(File pFile)
	{
		List<Event> events = decodeFile(pFile);
		Map<String, List<Event>> componentMap = new HashMap<String, List<Event>>();
		Map<String, List<Event>> eventTypeMap = new HashMap<String, List<Event>>();
		
		for ( Event evt : events )
		{
			if ( !componentMap.containsKey(evt.getComponentName()) ){
				componentMap.put(evt.getComponentName(), new ArrayList<Event>());
			}
			if ( !eventTypeMap.containsKey(evt.getType()) ){
				eventTypeMap.put(evt.getType(), new ArrayList<Event>());
			}
			componentMap.get(evt.getComponentName()).add(evt);
			eventTypeMap.get(evt.getType()).add(evt);
		}
		
		mCaller.reloadTree(buildComponentNodes(componentMap));
	}
	
	private List<MutableTreeNode> buildComponentNodes( Map<String, List<Event>> pComponentMap )
	{
		List<MutableTreeNode> nodes = new ArrayList<MutableTreeNode>();
		for ( String componentName : pComponentMap.keySet() )
		{
			MutableTreeNode componentNode = new DefaultMutableTreeNode(componentName);
			for ( Event evt : pComponentMap.get(componentName)) {
				MutableTreeNode eventNode = EventNodeFactory.getInstance().createNode(evt, false, true);
				componentNode.insert(eventNode, componentNode.getChildCount());
			}
			nodes.add(componentNode);
		}
		return nodes;
	}
	
	private List<Event> decodeFile(File pFile)
	{
		XMLEventHandler gestionnaire = new XMLEventHandler();
		try {
			SAXParserFactory fabrique = SAXParserFactory.newInstance();
			SAXParser parseur = fabrique.newSAXParser();
			parseur.parse(pFile, gestionnaire);
		} catch (IOException pExc) {
			LOGGER.error(pExc);
		} catch (SAXException pExc) {
			LOGGER.error(pExc);
		} catch (ParserConfigurationException pExc) {
			LOGGER.error(pExc);
		}
		return gestionnaire.getDecodedEvent();
	}

	private MainUI mCaller;
	private static final Logger LOGGER = Logger.getLogger(ImportAction.class);
}