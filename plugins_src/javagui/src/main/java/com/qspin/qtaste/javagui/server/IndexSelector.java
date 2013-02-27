package com.qspin.qtaste.javagui.server;

import javax.swing.JComboBox;
import javax.swing.JList;

import com.qspin.qtaste.testsuite.QTasteTestFailException;

class IndexSelector extends UpdateComponentCommander {

	protected int mIndex;
	
	@Override
	protected void prepareActions() throws QTasteTestFailException {
		mIndex = Integer.parseInt(mData[1].toString());
		if (component instanceof JComboBox) {
			JComboBox combo = (JComboBox) component;
			if (combo.getItemCount() < mIndex) {
				throw new QTasteTestFailException("Specified index is out of bounds");
			}
		} else if (component instanceof JList) {
			JList list = (JList) component;
			if (list.getModel().getSize() < mIndex) {
				throw new QTasteTestFailException("Specified index is out of bounds");
			}
		} else {
			throw new QTasteTestFailException("Unsupported component");
		}
	}

	@Override
	protected void doActionsInSwingThread() {
		if (component instanceof JComboBox) {
			((JComboBox) component).setSelectedIndex(mIndex);
		} else if (component instanceof JList) {
			((JList) component).setSelectedIndex(mIndex);
		}
	}

}
