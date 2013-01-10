package com.qspin.qtaste.tools.converter.ui.event;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.qspin.qtaste.tools.converter.model.event.Event;

public abstract class AbstractSpecificEventPane extends JPanel {

	public AbstractSpecificEventPane(String pPanetitle)
	{
		super();
		setLayout(new BorderLayout());
		add(createUI(), BorderLayout.CENTER);
		setBorder(BorderFactory.createTitledBorder(pPanetitle));
	}

	public abstract void resetFor(Event pEvt);
	protected abstract JPanel createUI();
}
