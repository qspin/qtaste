package com.qspin.qtaste.tools.factory.python;

import com.qspin.qtaste.tools.model.event.DocumentEvent;
import com.qspin.qtaste.tools.model.event.Event;

class PythonDocumentEventFactory extends PythonEventFactory {

	@Override
	public String createPythonEvent(Event pEvent, long pPreviousTimestamp) {
		DocumentEvent evt = (DocumentEvent) pEvent;
		StringBuilder builder = new StringBuilder();

		String command = "javaguiMI.setText(" + getComponentIdentifier(evt.getComponentName()) + ", \"";
		if (!evt.getChange().contains(LINE_BREAK)) {
			 command += evt.getChange();
		} else {
			command += evt.getChange().replace(LINE_BREAK, "\\n");
		}
		command += "\")";
		insertSleep(pEvent, pPreviousTimestamp, builder);
		builder.append(getPythonIndentation(1) + command + LINE_BREAK);
		return builder.toString();
	}

}
