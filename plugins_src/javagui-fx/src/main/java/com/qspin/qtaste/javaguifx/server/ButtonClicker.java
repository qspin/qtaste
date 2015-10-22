package com.qspin.qtaste.javaguifx.server;

import com.qspin.qtaste.testsuite.QTasteTestFailException;

import javafx.scene.control.ButtonBase;

/**
 * Component controller that simulates a click on an {@link ButtonBase} through the fire method.
 * The arm() method call will be executed in the Event Thread.
 * @author simjan
 *
 */
class ButtonClicker extends UpdateComponentCommander {

	@Override
	protected void prepareActions() throws QTasteTestFailException {}

	/**
	 * Simulates the click on the button through a call to the fire method.
	 */
	@Override
	protected void doActionsInEventThread() {
		((ButtonBase) component).fire();
	}

}
