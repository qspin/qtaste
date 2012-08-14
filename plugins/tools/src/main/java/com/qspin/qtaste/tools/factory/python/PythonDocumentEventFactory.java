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
	//		builder.append(getPythonIndentation(1) + "text = javaguiMI.getText(\"" + getComponentIdentifier(evt.getComponentName()) + "\")" + LINE_BREAK);
	//		builder.append(getPythonIndentation(1) + "newText = \"\"" + LINE_BREAK);
	//		builder.append(getPythonIndentation(1) + "for i in range(0, " + evt.getOffset() +  "):" + LINE_BREAK);
	//		builder.append(getPythonIndentation(2) +      "newText = newText + text[i]" + LINE_BREAK);
	//		if ( !evt.getDocumentChangeType().equals("DELETE"))
	//		{
	//			builder.append(getPythonIndentation(1) + "newText = newText + \"" + evt.getChange() + "\"" + LINE_BREAK);
	//		}
	//		builder.append(getPythonIndentation(1) + "for i in range(" + evt.getOffset()+evt.getLenght() + ", len(text)):" + LINE_BREAK);
	//		builder.append(getPythonIndentation(2) +      "newText = newText + text[i]" + LINE_BREAK);
	
	//		builder.append(getPythonIndentation(1) + "javaguiMI.setText(\"" + getComponentIdentifier(evt.getComponentName()) + "\", newText)" + LINE_BREAK);
			builder.append(getPythonIndentation(1) + "javaguiMI.setText(" + getComponentIdentifier(evt.getComponentName()) + ", \"" + evt.getChange() + "\")" + LINE_BREAK);
		}
		return builder.toString();
	}

}
