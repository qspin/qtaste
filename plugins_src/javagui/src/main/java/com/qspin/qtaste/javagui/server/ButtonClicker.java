package com.qspin.qtaste.javagui.server;

import javax.swing.AbstractButton;

class ButtonClicker extends UpdateComponentCommander {

	@Override
	public void run() {
		try {
			AbstractButton btn = (AbstractButton) component;					
			btn.doClick((Integer)mData[1]);
		} catch (Exception exc) {
			mError = exc;
		}
	}

}
