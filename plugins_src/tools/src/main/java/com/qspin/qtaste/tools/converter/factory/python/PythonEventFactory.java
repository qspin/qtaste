package com.qspin.qtaste.tools.converter.factory.python;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.qspin.qtaste.tools.converter.model.ComponentNameMapping;
import com.qspin.qtaste.tools.converter.model.event.ActionEvent;
import com.qspin.qtaste.tools.converter.model.event.ChangeEvent;
import com.qspin.qtaste.tools.converter.model.event.DocumentEvent;
import com.qspin.qtaste.tools.converter.model.event.Event;
import com.qspin.qtaste.tools.converter.model.event.TreeSelectionEvent;

public abstract class PythonEventFactory implements PythonEventFactoryInterface {

	
	public static String createEvent(Event pEvent, long pPreviousTimestamp)
	{
		if ( FACTORIES.containsKey(pEvent.getClass()) )
		{
			return FACTORIES.get(pEvent.getClass()).createPythonEvent(pEvent, pPreviousTimestamp);
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
	
	protected void insertSleep(Event pEvent, long pPreviousTimestamp, StringBuilder pBuilder )
	{
		if ( pPreviousTimestamp != Long.MIN_VALUE )
		{
			long sleepingTime = pEvent.getTimeStamp()-pPreviousTimestamp;
			if ( sleepingTime < 1000 ) {
				pBuilder.append(getPythonIndentation(1) + "# has to sleep " + sleepingTime + "ms" + LINE_BREAK);
			}
			else
			{
				pBuilder.append( getPythonIndentation(1) +"time.sleep(" + sleepingTime/1000 + ")" + LINE_BREAK);
			}
		}
	}
	
	protected String getComponentIdentifier(String pComponentName)
	{
		ComponentNameMapping mapping = ComponentNameMapping.getInstance();
		if ( mapping.hasAlias(pComponentName) )
		{
			return mapping.getAliasFor(pComponentName);
		} else {
			return "\"" + pComponentName + "\"";
		}
	}
	
	protected PythonEventFactory(){}
	
	protected static final Logger LOGGER = Logger.getLogger(PythonEventFactory.class);
	protected static final String LINE_BREAK = System.getProperty("line.separator");

	private static final String INDENTATION = "	";
	private static final Map<Class <? extends Event>, PythonEventFactoryInterface> FACTORIES = new HashMap<Class <? extends Event>, PythonEventFactoryInterface>();
	static
	{
		FACTORIES.put(ActionEvent.class, new PythonActionEventFactory());
		FACTORIES.put(ChangeEvent.class, new PythonChangeEventFactory());
		FACTORIES.put(DocumentEvent.class, new PythonDocumentEventFactory());
		FACTORIES.put(TreeSelectionEvent.class, new PythonTreeSelectionEventFactory());
	}
	
	
	
}
