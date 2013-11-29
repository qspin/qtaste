package com.qspin.qtaste.tools.converter.factory.python;

import com.qspin.qtaste.tools.converter.model.event.ChangeEvent;
import com.qspin.qtaste.tools.converter.model.event.Event;

class PythonChangeEventFactory extends PythonEventFactory {

	@Override
	public String createPythonEvent(Event pEvent, long pPreviousTimestamp)
	{
		ChangeEvent evt = (ChangeEvent)pEvent;
		StringBuilder builder = new StringBuilder();
		insertSleep(pEvent, pPreviousTimestamp, builder);
		builder.append(getPythonIndentation(1) + "javaguiMI.selectTab(" + getComponentIdentifier(evt.getComponentName()) + ", " + evt.getTabIndex() + ")" + LINE_BREAK);
		return builder.toString();
	}
	
}
