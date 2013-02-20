package com.qspin.qtaste.javagui.server;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;

import com.qspin.qtaste.testsuite.QTasteTestFailException;

public class ValueSelector extends UpdateComponentCommander {

	@Override
	public void run() {
		try {
			String value = mData[1].toString();
			if (component instanceof JCheckBox || component instanceof JRadioButton) {
				new ComponentSelector().executeCommand(mData[0].toString(), Boolean.parseBoolean(value));
			}
			if (component instanceof JComboBox) {
				JComboBox combo = (JComboBox) component;
				for (int i = 0; i < combo.getItemCount(); i++) { //
					// Use a startsWith instead of equals() as toString()
					// can
					// return more than the value
					if ((combo.getItemAt(i)).toString().startsWith(value)) {
						combo.setSelectedIndex(i);
						return;
					}
				}
			}
			if (component instanceof JList) {
				JList list = (JList) component;
				for (int i = 0; i < list.getModel().getSize(); i++) {
					if (list.getModel().getElementAt(i).toString().equals(value)) {
						list.setSelectedIndex(i);
						return;
					}
				}
				// TODO: Value not found! Send exception?
			}
			if (component instanceof JSpinner) {
				JSpinner spinner = (JSpinner) component;
				spinner.getModel().setValue(Double.parseDouble(value));
			}
			if (component instanceof JSlider) {
				JSlider slider = (JSlider) component;
				slider.getModel().setValue(Integer.parseInt(value));
				return;
			} else {
				throw new QTasteTestFailException("component '" + component.getName() + "' (" + component.getClass() + ") found but unused");
			}
		} catch (Exception pExc) {
			mError = pExc;
		}
	}

}
