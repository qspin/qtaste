package com.qspin.qtaste.controlscriptbuilder.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.qspin.qtaste.config.TestBedConfiguration;
import com.qspin.qtaste.controlscriptbuilder.io.ControlScriptDecoder;
import com.qspin.qtaste.controlscriptbuilder.model.ControlAction;
import com.qspin.qtaste.controlscriptbuilder.ui.editor.Editor;
import com.qspin.qtaste.controlscriptbuilder.ui.model.ControlActionsTableModel;

@SuppressWarnings("serial")
public class ControlScriptBuilderPane extends JPanel implements ListSelectionListener{

	public ControlScriptBuilderPane()
	{
		super();
		genUI();
		this.addFocusListener(new MyFocusListener());
		
	}

	public void setControlActions(List<ControlAction> actions) {
		((ControlActionsTableModel)mTable.getModel()).setControlActions(actions);
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if ( e.getSource() == mTable.getSelectionModel() )
		{
			boolean isSelected = mTable.getSelectedRow() >= 0;
//			mDeleteButton.setEnabled(isSelected);
			mEditButton.setEnabled(isSelected);
			
//			mUpButton.setEnabled(mTable.getSelectedRow()>0);
//			mDownButton.setEnabled(mTable.getSelectedRow()<mTable.getModel().getRowCount()-1);
		}
	}
	
	public void reload()
	{
		List<ControlAction> actions = new ArrayList<ControlAction>();
		String controlScriptFileName = TestBedConfiguration.getInstance().getControlScriptFileName();
		String paramFileName = controlScriptFileName.substring(0, controlScriptFileName.length()-3) + ".param";
		File paramFile = new File(paramFileName);
		LOGGER.info("parameter file existence ? " + paramFile.exists());
		if ( paramFile.exists() )
		{
			//decode the control script
			try
			{
				actions = ControlScriptDecoder.decode(paramFile);
			}
			catch (IOException pExc)
			{
				LOGGER.error("Unable to decode the control script: " + pExc.getMessage(), pExc);
			}
		}
		setControlActions(actions);
	}
	
	private void genUI()
	{
		FormLayout layout = new FormLayout("3dlu, pref:grow, 3dlu, pref, 3dlu",
										   "3dlu, top:pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, top:pref:grow, 3dlu, pref, 3dlu");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		int rowIndex = 2;
		
		loadComponents();
		
		builder.add(new JScrollPane(mTable), cc.xywh(2, rowIndex, 1, 10));
		builder.add(mUpButton, cc.xy(4, rowIndex));
		rowIndex += 2;

		builder.add(mDownButton, cc.xy(4, rowIndex));
		rowIndex += 2;

		builder.add(mNewButton, cc.xy(4, rowIndex));
		rowIndex += 2;

		builder.add(mEditButton, cc.xy(4, rowIndex));
		rowIndex += 2;

		builder.add(mDeleteButton, cc.xy(4, rowIndex));
		rowIndex += 2;
		
		setLayout(new BorderLayout());
		add(builder.getPanel(), BorderLayout.CENTER);
	}
	
	private void loadComponents()
	{
		mTable = new JTable(new ControlActionsTableModel());
		mTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mTable.getSelectionModel().addListSelectionListener(this);
		
		ButtonActions ba = new ButtonActions();
		
		mUpButton = new JButton("Move up");
		mUpButton.setEnabled(false);
		mUpButton.addActionListener(ba);
		
		mDownButton = new JButton("Move down");
		mDownButton.setEnabled(false);
		mDownButton.addActionListener(ba);
		
		mNewButton = new JButton("Add");
		mNewButton.addActionListener(ba);
		
		mEditButton = new JButton("Edit");
		mEditButton.setEnabled(false);
		mEditButton.addActionListener(ba);
		
		mDeleteButton = new JButton("Delete");
		mDeleteButton.setEnabled(false);
		mDeleteButton.addActionListener(ba);
		
		mEditor = new Editor();
	}
	
	private JTable mTable;
	private JButton mUpButton;
	private JButton mDownButton;
	private JButton mNewButton;
	private JButton mEditButton;
	private JButton mDeleteButton;
	private Editor mEditor;
	private long loadDateAndTime;
	
	protected static final Logger LOGGER = Logger.getLogger(ControlScriptBuilderPane.class);

    private class MyFocusListener implements FocusListener {

        public void focusGained(FocusEvent e) {
            // check if the file has been modified outside the editor
            File file = new File(TestBedConfiguration.getInstance().getControlScriptFileName());
            if ( file.exists() )
            {
	            // check date and time
	            long lastFileModifiedDate = file.lastModified();
	            if (loadDateAndTime < lastFileModifiedDate) {
	                loadDateAndTime = lastFileModifiedDate;
                    reload();
	            }
            }
        }

        public void focusLost(FocusEvent e) {
        }
    }
    
	private class ButtonActions implements ActionListener
	{
		public void actionPerformed(ActionEvent pEvt)
		{
			ControlActionsTableModel model = (ControlActionsTableModel) mTable.getModel();
			int idx = mTable.getSelectedRow();
			if ( pEvt.getSource() == mDeleteButton )
			{
				model.remove(idx);
			}
			else if ( pEvt.getSource() == mUpButton )
			{
				model.moveUp(idx);
				mTable.getSelectionModel().setSelectionInterval(-1, idx+1);
			}
			else if ( pEvt.getSource() == mDownButton )
			{
				model.moveDown(idx);
				mTable.getSelectionModel().setSelectionInterval(-1, idx-1);
			}
			else if ( pEvt.getSource() == mEditButton )
			{
				mEditor.loadControlAction(((ControlActionsTableModel)mTable.getModel()).getControlAction(idx));
				mEditor.setVisible(true);
			}
		}
	}
}
