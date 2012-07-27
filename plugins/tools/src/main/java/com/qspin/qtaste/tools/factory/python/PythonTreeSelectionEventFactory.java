package com.qspin.qtaste.tools.factory.python;

import com.qspin.qtaste.tools.model.event.Event;
import com.qspin.qtaste.tools.model.event.TreeSelectionEvent;

public class PythonTreeSelectionEventFactory extends PythonEventFactory {

	@Override
	public String createPythonEvent(Event pEvent) {
		TreeSelectionEvent evt = (TreeSelectionEvent)pEvent;
		StringBuilder builder = new StringBuilder();
		
		builder.append(getPythonIndentation(1) + "javaguiMI.selectNode(\"" + evt.getComponentName() + "\", ");
		builder.append(												  "\"" + evt.getSelectedPath().substring(1, evt.getSelectedPath().length()-1) + "\", " );
		builder.append(												  "\", \"" + " )" + LINE_BREAK);
		return builder.toString();
	}
	
}
