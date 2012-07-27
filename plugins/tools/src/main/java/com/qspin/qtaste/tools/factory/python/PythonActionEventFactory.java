package com.qspin.qtaste.tools.factory.python;

import com.qspin.qtaste.tools.model.event.ActionEvent;
import com.qspin.qtaste.tools.model.event.Event;

public class PythonActionEventFactory extends PythonEventFactory {

	@Override
	public String createPythonEvent(Event pEvent) {
		ActionEvent evt = (ActionEvent)pEvent;
		StringBuilder builder = new StringBuilder();
		builder.append(getPythonIndentation(1) + "javaguiMI.clickOnButton(\"" + evt.getComponentName() + "\")" + LINE_BREAK);
		return builder.toString();
	}
	
}
