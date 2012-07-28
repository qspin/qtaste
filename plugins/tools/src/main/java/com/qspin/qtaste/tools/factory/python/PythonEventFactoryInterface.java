package com.qspin.qtaste.tools.factory.python;

import com.qspin.qtaste.tools.model.event.Event;


public interface PythonEventFactoryInterface {

	String createPythonEvent(Event pEvent, long pPreviousTimestamp);
}
