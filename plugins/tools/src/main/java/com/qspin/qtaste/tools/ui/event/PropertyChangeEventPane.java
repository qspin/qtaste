package com.qspin.qtaste.tools.ui.event;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.qspin.qtaste.tools.model.event.Event;
import com.qspin.qtaste.tools.model.event.PropertyChangeEvent;

public class PropertyChangeEventPane extends AbstractSpecificEventPane {

	public PropertyChangeEventPane()
	{
		super("Property change event data");
	}

	@Override
	protected JPanel createUI() {
		setLayout(new BorderLayout());
		FormLayout layout = new FormLayout("right:pref, 3dlu, pref:grow", "pref, 3dlu, pref, 3dlu, pref");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		int rowIndex = 1;
		
		builder.addLabel("Property name : ", cc.xy(1,rowIndex));
		mPropertyName = new JLabel();
		builder.add(mPropertyName, cc.xy(3,rowIndex));
		rowIndex += 2;
		
		builder.addLabel("Old value : ", cc.xy(1,rowIndex));
		mPropertyOldValue = new JLabel();
		builder.add(mPropertyOldValue, cc.xy(3,rowIndex));
		rowIndex += 2;
		
		builder.addLabel("New value : ", cc.xy(1,rowIndex));
		mPropertyNewValue = new JLabel();
		builder.add(mPropertyNewValue, cc.xy(3,rowIndex));
		rowIndex += 2;
		
		return builder.getPanel();
	}

	@Override
	public void resetFor(Event pEvt) {
		if ( pEvt instanceof PropertyChangeEvent )
		{
			PropertyChangeEvent evt = (PropertyChangeEvent)pEvt;
			mPropertyName.setText(evt.getPropertyName());
			mPropertyOldValue.setText(formatText(evt.getOldValue()));
			mPropertyNewValue.setText(formatText(evt.getNewValue()));
		}
		else
		{
			mPropertyName.setText(null);
			mPropertyOldValue.setText(null);
			mPropertyNewValue.setText(null);	
		}
	}
	
	private String formatText(String pText)
	{
		if ( pText != null && pText.length() > MAX_TEXT_LENGTH )
		{
			return pText.substring(0, MAX_TEXT_LENGTH) + "...";
		}
		return pText;
	}

	private JLabel mPropertyName;
	private JLabel mPropertyOldValue;
	private JLabel mPropertyNewValue;
	
	private static final int MAX_TEXT_LENGTH = 140;
}
