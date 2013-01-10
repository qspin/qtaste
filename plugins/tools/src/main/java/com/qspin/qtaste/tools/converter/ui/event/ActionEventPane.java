package com.qspin.qtaste.tools.converter.ui.event;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.qspin.qtaste.tools.converter.model.event.ActionEvent;
import com.qspin.qtaste.tools.converter.model.event.Event;

public class ActionEventPane extends AbstractSpecificEventPane {

	public ActionEventPane()
	{
		super("Action event data");
	}

	@Override
	protected JPanel createUI() {
		setLayout(new BorderLayout());
		FormLayout layout = new FormLayout("right:pref, 3dlu, pref:grow", "pref, 3dlu, pref");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		int rowIndex = 1;
		
		builder.addLabel("ID : ", cc.xy(1,rowIndex));
		mId = new JLabel();
		builder.add(mId, cc.xy(3,rowIndex));
		rowIndex += 2;
		
		builder.addLabel("Action command : ", cc.xy(1,rowIndex));
		mActionCommand = new JLabel();
		builder.add(mActionCommand, cc.xy(3,rowIndex));
		rowIndex += 2;
		
		return builder.getPanel();
	}

	@Override
	public void resetFor(Event pEvt) {
		if ( pEvt instanceof ActionEvent )
		{
			mId.setText(((ActionEvent)pEvt).getId());
			mActionCommand.setText(((ActionEvent)pEvt).getActionCommand());
		}
		else
		{
			mId.setText(null);
			mActionCommand.setText(null);
		}
	}

	private JLabel mId;
	private JLabel mActionCommand;
}
