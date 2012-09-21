package com.qspin.qtaste.tools.converter.ui.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.qspin.qtaste.tools.converter.io.XMLEventHandler;
import com.qspin.qtaste.tools.converter.model.ComponentNameMapping;
import com.qspin.qtaste.tools.converter.model.EventManager;
import com.qspin.qtaste.tools.converter.model.event.Event;
import com.qspin.qtaste.tools.converter.ui.MainUI;

public class ImportAction implements ActionListener {

	public ImportAction(MainUI pCaller) {
		mCaller = pCaller;
	}

	public void actionPerformed(ActionEvent pEvent) {
		JFileChooser jfc = new JFileChooser();
		if (jfc.showOpenDialog(mCaller) == JFileChooser.APPROVE_OPTION) {
			EventManager.getInstance().setEvents(decodeFile(jfc.getSelectedFile()));
			try {
				ComponentNameMapping.getInstance().load();
			} catch (IOException pExc)
			{
				LOGGER.error(pExc);
			}
		}
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
		ComponentNameMapping.getInstance().setFilePath(pFile.getParent() +File.separator + ComponentNameMapping.ALIAS_FILE_NAME);
		return gestionnaire.getDecodedEvent();
	}

	private MainUI mCaller;
	private static final Logger LOGGER = Logger.getLogger(ImportAction.class);
}