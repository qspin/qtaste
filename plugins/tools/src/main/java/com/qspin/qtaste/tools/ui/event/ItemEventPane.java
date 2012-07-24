package com.qspin.qtaste.tools.ui.event;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.qspin.qtaste.tools.model.event.Event;
import com.qspin.qtaste.tools.model.event.ItemEvent;

public class ItemEventPane extends AbstractSpecificEventPane {

	public ItemEventPane()
	{
		super("Item event data");
	}

	@Override
	protected JPanel createUI() {
		setLayout(new BorderLayout());
		FormLayout layout = new FormLayout("right:pref, 3dlu, pref:grow", "pref, 3dlu, pref, 3dlu, pref");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		int rowIndex = 1;
		
		builder.addLabel("State changed : ", cc.xy(1,rowIndex));
		mId = new JLabel();
		builder.add(mId, cc.xy(3,rowIndex));
		rowIndex += 2;
		
		builder.addLabel("ID : ", cc.xy(1,rowIndex));
		mStateChanged = new JLabel();
		builder.add(mStateChanged, cc.xy(3,rowIndex));
		rowIndex += 2;
		
		builder.addLabel("Item : ", cc.xy(1,rowIndex));
		mItem = new JLabel();
		builder.add(mItem, cc.xy(3,rowIndex));
		rowIndex += 2;
		
		return builder.getPanel();
	}

	@Override
	public void resetFor(Event pEvt) {
		if ( pEvt instanceof ItemEvent )
		{
			mId.setText(((ItemEvent)pEvt).getId());
			mStateChanged.setText(((ItemEvent)pEvt).getState());
			mItem.setText(((ItemEvent)pEvt).getSelectedItem());
		}
		else
		{
			mId.setText(null);
			mStateChanged.setText(null);
			mItem.setText(null);
		}
	}

	private JLabel mId;
	private JLabel mStateChanged;
	private JLabel mItem;
}
