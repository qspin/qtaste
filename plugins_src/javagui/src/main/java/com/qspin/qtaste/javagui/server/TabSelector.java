package com.qspin.qtaste.javagui.server;

import javax.swing.JTabbedPane;

class TabSelector extends UpdateComponentCommander {

	@Override
	public void run() {
		try
		{
			int tabIndex = Integer.parseInt(mData[1].toString());
			if (component != null && component instanceof JTabbedPane) {
				((JTabbedPane)component).setSelectedIndex(tabIndex);
			}
		}
		catch (Exception pExc)
		{
			mError = pExc;
		}
	}

}
