package com.qspin.qtaste.javagui.server;

import javax.swing.JComboBox;
import javax.swing.JList;

import com.qspin.qtaste.testsuite.QTasteTestFailException;

/**
 * Component controller responsible for the selection of a specific index on a {@link JComboBox} or on a {@link JList}.
 * This selection is processed by the Swing Thread.
 * 
 * @author simjan
 *
 */
class IndexSelector extends UpdateComponentCommander {

	protected int mIndex;
	
	/**
	 * Takes the index in the user data (the second parameter) and checks the component's type.
	 * @throws QTasteTestFailException if the component is not a JComboBox or a JList.
	 */
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

	/**
	 * Selects the index.
	 */
	@Override
	protected void doActionsInSwingThread() {
		if (component instanceof JComboBox) {
			((JComboBox) component).setSelectedIndex(mIndex);
		} else if (component instanceof JList) {
			((JList) component).setSelectedIndex(mIndex);
		}
	}

}
