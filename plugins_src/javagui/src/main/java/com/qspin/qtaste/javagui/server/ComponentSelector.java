package com.qspin.qtaste.javagui.server;

import javax.swing.AbstractButton;

import com.qspin.qtaste.testsuite.QTasteTestFailException;

/**
 * Component controller that simulates the (de)selection of an {@link AbstractButton}.
 * The change is done through the setSelected method. The method call is done in the Swing Thread.
 * 
 * @author simjan
 *
 */
class ComponentSelector extends UpdateComponentCommander {

	protected boolean mSelectState;
	
	/**
	 * Takes from the data the selection state.
	 * @throws QTasteTestFailException if the component is not an {@link AbstractButton}.
	 */
	@Override
	protected void prepareActions() throws QTasteTestFailException {
		mSelectState = Boolean.parseBoolean(mData[2].toString());
		if ( !(component instanceof AbstractButton) ){
			throw new QTasteTestFailException("Unsupported component.");
		}
	}

	@Override
	protected void doActionsInSwingThread() {
		((AbstractButton) component).setSelected(mSelectState);
	}

}
