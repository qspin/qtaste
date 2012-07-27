package com.qspin.qtaste.tools.factory.python;

import com.qspin.qtaste.tools.model.event.DocumentEvent;
import com.qspin.qtaste.tools.model.event.Event;

public class PythonDocumentEventFactory extends PythonEventFactory {

	@Override
	public String createPythonEvent(Event pEvent) {
		DocumentEvent evt = (DocumentEvent)pEvent;
		StringBuilder builder = new StringBuilder();
		
		builder.append(getPythonIndentation(1) + "text = javaguiMI.getText(\"" + evt.getComponentName() + "\")" + LINE_BREAK);
		builder.append(getPythonIndentation(1) + "newText = \"\"" + LINE_BREAK);
		builder.append(getPythonIndentation(1) + "for i in range(0, " + evt.getOffset() +  "):" + LINE_BREAK);
		builder.append(getPythonIndentation(2) +      "newText = newText + text[i]" + LINE_BREAK);
		if ( !evt.getDocumentChangeType().equals("DELETE"))
		{
			builder.append(getPythonIndentation(1) + "newText = newText + \"" + evt.getChange() + "\"" + LINE_BREAK);
		}
		builder.append(getPythonIndentation(1) + "for i in range(" + evt.getOffset()+evt.getLenght() + ", len(text)):" + LINE_BREAK);
		builder.append(getPythonIndentation(2) +      "newText = newText + text[i]" + LINE_BREAK);
		
		builder.append(getPythonIndentation(1) + "text = javaguiMI.setText(\"" + evt.getComponentName() + "\", newText)" + LINE_BREAK);
		
		return builder.toString();
	}

}
