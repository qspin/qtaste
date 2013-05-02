package com.qspin.qtaste.javagui.server;

import javax.swing.JTabbedPane;

import com.qspin.qtaste.testsuite.QTasteTestFailException;

class TabSelector extends UpdateComponentCommander {

	protected int mTabIndex;

	@Override
	protected void prepareActions() throws QTasteTestFailException {
		mTabIndex = Integer.parseInt(mData[2].toString());
	}

	@Override
	protected void doActionsInSwingThread() {
		if (component != null && component instanceof JTabbedPane) {
			((JTabbedPane)component).setSelectedIndex(mTabIndex);
		}
	}

}
