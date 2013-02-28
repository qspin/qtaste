package com.qspin.qtaste.sutuidemo;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

class CustomListCellRenderer extends DefaultListCellRenderer {

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if ( value instanceof Person )
		{
			Person p = (Person) value;
			setText(p.getFirstName() + " " + p.getLastName() + " (" + p.getAge() + ")");
		}
		return this;
	}

}
