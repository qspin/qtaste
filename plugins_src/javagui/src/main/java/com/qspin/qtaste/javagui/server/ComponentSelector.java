package com.qspin.qtaste.javagui.server;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;

import com.qspin.qtaste.testsuite.QTasteTestFailException;

/**
 * Component controller that simulates the (de)selection of a {@link JCheckBox} or a {@link JRadioButton}.
 * The change is done through the setSelected method. The method call is done in the Swing Thread.
 * 
 * @author simjan
 *
 */
class ComponentSelector extends UpdateComponentCommander {

	protected boolean mSelectState;
	
	/**
	 * Takes from the data the selection state.
	 * @throws QTasteTestFailException if the component is not a {@link JComboBox} or a {@link JRadioButton}.
	 */
	@Override
	protected void prepareActions() throws QTasteTestFailException {
		mSelectState = Boolean.parseBoolean(mData[2].toString());
		if ( !(component instanceof JCheckBox) && !(component instanceof JRadioButton) ){
			throw new QTasteTestFailException("Unsupported component.");
		}
	}

	@Override
	protected void doActionsInSwingThread() {
		if (component instanceof JCheckBox) {
			((JCheckBox) component).setSelected(mSelectState);
		} else if (component instanceof JRadioButton) {
			((JRadioButton) component).setSelected(mSelectState);
		}
	}

}
