package com.qspin.qtaste.javagui.server;

import javax.swing.AbstractButton;

import com.qspin.qtaste.testsuite.QTasteTestFailException;

/**
 * Component controller that simulates a click on an {@link AbtractButton} through the doClick method.
 * The doClick(int) method call will be executed in the Swing Thread.
 * @author simjan
 *
 */
class ButtonClicker extends UpdateComponentCommander {

	protected int mPressTime;

	/**
	 * Takes from the data the time to hold down the button.
	 */
	@Override
	protected void prepareActions() throws QTasteTestFailException {
		mPressTime = (Integer)mData[1];
	}

	/**
	 * Simulates the click on the button through a call to the doClick method.
	 */
	@Override
	protected void doActionsInSwingThread() {
		((AbstractButton) component).doClick(mPressTime);
	}

}
