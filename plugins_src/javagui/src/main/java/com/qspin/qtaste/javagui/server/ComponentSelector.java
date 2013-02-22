package com.qspin.qtaste.javagui.server;

import javax.swing.JCheckBox;
import javax.swing.JRadioButton;

class ComponentSelector extends UpdateComponentCommander {

	@Override
	public void run() {
		try
		{
			boolean value = Boolean.parseBoolean(mData[1].toString());
			if (component instanceof JCheckBox) {
				((JCheckBox) component).setSelected(value);
			} else if (component instanceof JRadioButton) {
				((JRadioButton) component).setSelected(value);
			}
		} catch (Exception pExc)
		{
			mError = pExc;
		}
	}

}
