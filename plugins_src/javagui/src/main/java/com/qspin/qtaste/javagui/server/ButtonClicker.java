package com.qspin.qtaste.javagui.server;

import javax.swing.AbstractButton;

import com.qspin.qtaste.testsuite.QTasteTestFailException;

class ButtonClicker extends UpdateComponentCommander {

	protected int mPressTime;

	@Override
	protected void prepareActions() throws QTasteTestFailException {
		mPressTime = (Integer)mData[1];
	}

	@Override
	protected void doActionsInSwingThread() {
		((AbstractButton) component).doClick(mPressTime);
	}

}
