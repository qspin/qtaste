package com.qspin.qtaste.javagui.server;

import java.awt.Component;
import java.awt.Label;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.ListCellRenderer;

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
				ListCellRenderer renderer = combo.getRenderer();
				for (int i = 0; i < combo.getItemCount(); i++) { 
					String itemValue = getItemText(combo.getModel().getElementAt(i), renderer);
					System.out.println("compare combo elmt (" + itemValue + ") with '" + value + "'");
					// Use a startsWith instead of equals() as toString() can return more than the value
					if (itemValue.equals(value)) {
						combo.setSelectedIndex(i);
						return;
					}
				}
			}
			if (component instanceof JList) {
				JList list = (JList) component;
				ListCellRenderer renderer = list.getCellRenderer();
				for (int i = 0; i < list.getModel().getSize(); i++) {
					String itemValue = getItemText(list.getModel().getElementAt(i), renderer);
					System.out.println("compare list elmt (" + itemValue + ") with '" + value + "'");
					if (itemValue.equals(value)) {
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
	
	protected String getItemText(Object item, ListCellRenderer renderer)
	{
		Component c = renderer.getListCellRendererComponent(new JList(), item, 0, false, false);
		if ( c instanceof Label )
		{
			return ((Label)c).getText();
		}
		if ( c instanceof JLabel )
		{
			return ((JLabel)c).getText();
		}
		return item.toString();
	}

}
