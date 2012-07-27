package com.qspin.qtaste.tools.factory.python;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.qspin.qtaste.tools.model.event.ActionEvent;
import com.qspin.qtaste.tools.model.event.DocumentEvent;
import com.qspin.qtaste.tools.model.event.Event;
import com.qspin.qtaste.tools.model.event.TreeSelectionEvent;

public abstract class PythonEventFactory implements PythonEventFactoryInterface {

	
	public static String createEvent(Event pEvent)
	{
		if ( FACTORIES.containsKey(pEvent.getClass()) )
		{
			return FACTORIES.get(pEvent.getClass()).createPythonEvent(pEvent);
		}
		LOGGER.warn("Cannot convert an event of " + pEvent.getClass());
		return "";
	}
	
	protected String getPythonIndentation(int pLevel) {
		StringBuilder builder = new StringBuilder();
		for ( int i = 0;  i< pLevel; ++i)
		{
			builder.append(INDENTATION);
		}
		return builder.toString();
	}
	
	protected PythonEventFactory(){}
	
	protected static final Logger LOGGER = Logger.getLogger(PythonEventFactory.class);
	protected static final String LINE_BREAK = System.getProperty("line.separator");

	private static final String INDENTATION = "	";
	private static final Map<Class <? extends Event>, PythonEventFactoryInterface> FACTORIES = new HashMap<Class <? extends Event>, PythonEventFactoryInterface>();
	static
	{
		FACTORIES.put(ActionEvent.class, new PythonActionEventFactory());
		FACTORIES.put(DocumentEvent.class, new PythonDocumentEventFactory());
		FACTORIES.put(TreeSelectionEvent.class, new PythonTreeSelectionEventFactory());
	}
	
	
	
}
