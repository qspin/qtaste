package com.qspin.qtaste.tools.converter.factory.python;

import com.qspin.qtaste.tools.converter.model.event.ActionEvent;
import com.qspin.qtaste.tools.converter.model.event.Event;

class PythonActionEventFactory extends PythonEventFactory {

	@Override
	public String createPythonEvent(Event pEvent, long pPreviousTimestamp)
	{
		ActionEvent evt = (ActionEvent)pEvent;
		StringBuilder builder = new StringBuilder();
		insertSleep(pEvent, pPreviousTimestamp, builder);
		builder.append(getPythonIndentation(1) + "javaguiMI.clickOnButton(" + getComponentIdentifier(evt.getComponentName()) + ")" + LINE_BREAK);
		return builder.toString();
	}
	
}
