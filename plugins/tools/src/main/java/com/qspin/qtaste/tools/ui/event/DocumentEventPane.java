package com.qspin.qtaste.tools.ui.event;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.qspin.qtaste.tools.model.event.DocumentEvent;
import com.qspin.qtaste.tools.model.event.Event;

public class DocumentEventPane extends AbstractSpecificEventPane {

	public DocumentEventPane()
	{
		super("Document event data");
	}

	@Override
	protected JPanel createUI() {
		setLayout(new BorderLayout());
		FormLayout layout = new FormLayout("right:pref, 3dlu, pref:grow", "pref, 3dlu, pref, 3dlu, pref, 3dlu, pref");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		int rowIndex = 1;
		
		builder.addLabel("Document change type : ", cc.xy(1,rowIndex));
		mDocumentChangeType = new JLabel();
		builder.add(mDocumentChangeType, cc.xy(3,rowIndex));
		rowIndex += 2;
		
		builder.addLabel("Offset : ", cc.xy(1,rowIndex));
		mOffset = new JLabel();
		builder.add(mOffset, cc.xy(3,rowIndex));
		rowIndex += 2;
		
		builder.addLabel("Length : ", cc.xy(1,rowIndex));
		mLenght = new JLabel();
		builder.add(mLenght, cc.xy(3,rowIndex));
		rowIndex += 2;
		
		builder.addLabel("Change : ", cc.xy(1,rowIndex));
		mChange = new JLabel();
		builder.add(mChange, cc.xy(3,rowIndex));
		rowIndex += 2;
		
		return builder.getPanel();
	}

	@Override
	public void resetFor(Event pEvt) {
		if ( pEvt instanceof DocumentEvent )
		{
			mDocumentChangeType.setText(((DocumentEvent)pEvt).getDocumentChangeType());
			mOffset.setText(Integer.toString(((DocumentEvent)pEvt).getOffset()));
			mLenght.setText(Integer.toString(((DocumentEvent)pEvt).getLenght()));
			mChange.setText(((DocumentEvent)pEvt).getChange());
		}
		else
		{
			mDocumentChangeType.setText(null);
			mOffset.setText(null);
			mLenght.setText(null);
			mChange.setText(null);
		}
	}

	private JLabel mDocumentChangeType;
	private JLabel mOffset;
	private JLabel mLenght;
	private JLabel mChange;
}
