package com.qspin.qtaste.javagui.server;

import javax.swing.JCheckBox;
import javax.swing.JRadioButton;

import com.qspin.qtaste.testsuite.QTasteTestFailException;

class ComponentSelector extends UpdateComponentCommander {

	protected boolean mSelectState;
	
	@Override
	protected void prepareActions() throws QTasteTestFailException {
		System.out.println("preparation");
		mSelectState = Boolean.parseBoolean(mData[1].toString());
		if ( !(component instanceof JCheckBox) && !(component instanceof JRadioButton) ){
			throw new QTasteTestFailException("Unsupported component.");
		}
		System.out.println("done");
	}

	@Override
	protected void doActionsInSwingThread() {
		System.out.println("action");
		if (component instanceof JCheckBox) {
			((JCheckBox) component).setSelected(mSelectState);
		} else if (component instanceof JRadioButton) {
			((JRadioButton) component).setSelected(mSelectState);
		}
		System.out.println("done");
	}

}
