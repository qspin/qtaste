package com.qspin.qtaste.tools.factory.python;

import com.qspin.qtaste.tools.model.event.DocumentEvent;
import com.qspin.qtaste.tools.model.event.Event;

class PythonDocumentEventFactory extends PythonEventFactory {

	@Override
	public String createPythonEvent(Event pEvent, long pPreviousTimestamp)
	{
		DocumentEvent evt = (DocumentEvent)pEvent;
		StringBuilder builder = new StringBuilder();

		if ( !evt.getChange().equals("\n") && !evt.getChange().equals(LINE_BREAK))
		{
			insertSleep(pEvent, pPreviousTimestamp, builder);
			builder.append(getPythonIndentation(1) + "javaguiMI.setText(" + getComponentIdentifier(evt.getComponentName()) + ", \"" + evt.getChange() + "\")" + LINE_BREAK);
		}
		return builder.toString();
	}

}
