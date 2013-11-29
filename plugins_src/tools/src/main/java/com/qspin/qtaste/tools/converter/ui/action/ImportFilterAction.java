package com.qspin.qtaste.tools.converter.ui.action;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.qspin.qtaste.tools.converter.filter.EventFilter;
import com.qspin.qtaste.tools.converter.model.EventManager;
import com.qspin.qtaste.tools.converter.ui.MainUI;
import com.qspin.qtaste.tools.filter.Filter;
import com.qspin.qtaste.tools.filter.FilterXmlHandler;

public class ImportFilterAction extends ImportAction {

	public ImportFilterAction(MainUI pCaller) {
		super(pCaller);
	}

	public void useSelectedFile(File pSelectedFile) {
		mCaller.setFilterDefinitionFile(pSelectedFile.getAbsolutePath());
		EventManager.getInstance().setEventsFilter(decodeFile(pSelectedFile));
	}

	private List<Filter> decodeFile(File pFile)
	{
		FilterXmlHandler gestionnaire = new FilterXmlHandler();
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
		List<Filter> filters = new ArrayList<Filter>();
		for (Filter f: gestionnaire.getDecodedFilters() )
		{
			filters.add(new EventFilter(f));
		}
		return filters;
	}
}