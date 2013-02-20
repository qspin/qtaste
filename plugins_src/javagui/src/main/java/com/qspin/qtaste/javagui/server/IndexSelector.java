package com.qspin.qtaste.javagui.server;

import javax.swing.JComboBox;
import javax.swing.JList;

class IndexSelector extends UpdateComponentCommander {

	@Override
	public void run() {
		try {
			int index = Integer.parseInt(mData[1].toString());
			if (component instanceof JComboBox) {
				JComboBox combo = (JComboBox) component;
				if (combo.getItemCount() > index) {
					combo.setSelectedIndex(index);
				}
			} else if (component instanceof JList) {
				JList list = (JList) component;
				if (list.getModel().getSize() > index) {
					list.setSelectedIndex(index);
				}
			}
		}catch (Exception pExc)
		{
			mError = pExc;
		}
	}

}
