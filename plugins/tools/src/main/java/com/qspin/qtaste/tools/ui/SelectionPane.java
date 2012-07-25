package com.qspin.qtaste.tools.ui;

import static com.qspin.qtaste.tools.ui.UIConstants.COMPONENT_SPACING;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public final class SelectionPane extends JPanel {

	public SelectionPane(String pItemType)
	{
		genUI(pItemType);
		setBorder(BorderFactory.createTitledBorder(pItemType));
	}
	
	public void resetFor(Object[] pAvailableValues) {
		DefaultListModel model = ((DefaultListModel)mAvailable.getModel());
		model.clear();
		for ( Object o : pAvailableValues )
		{
			model.add(model.size(), o);
		}
		((DefaultListModel)mSelected.getModel()).clear();
	}
	
	public List<Object> getSelectedItems()
	{
		return (List<Object>) Collections.list(((DefaultListModel)mSelected.getModel()).elements());
	}
	
	private void genUI(String pItemType)
	{
		FormLayout layout = new FormLayout( 
				"center:pref" + COMPONENT_SPACING + "pref" + COMPONENT_SPACING + "center:pref",
				"pref" + COMPONENT_SPACING + "30dlu, pref" + COMPONENT_SPACING + "pref, 30dlu");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();

		builder.addLabel("Available " + pItemType, cc.xy(1, 1));
		builder.addLabel("Selected " + pItemType, cc.xy(5, 1));

		mAvailable = new JList(new DefaultListModel());
		builder.add(createScrollPane(mAvailable), cc.xywh(1,3,1,5));
		
		mSelected = new JList(new DefaultListModel());
		builder.add(createScrollPane(mSelected), cc.xywh(5,3,1,5));

		JButton toRight = new JButton("=>");
		toRight.setEnabled(false);
		toRight.addActionListener(new MoveTo(mAvailable, mSelected));
		builder.add(toRight, cc.xy(3,4));
		JButton toLeft = new JButton("<=");
		toLeft.setEnabled(false);
		toLeft.addActionListener(new MoveTo(mSelected, mAvailable));
		builder.add(toLeft, cc.xy(3,6));
		
		mAvailable.addListSelectionListener(new SelectionListener(toRight));
		mSelected.addListSelectionListener(new SelectionListener(toLeft));
		
		setLayout(new BorderLayout());
		add(builder.getPanel(), BorderLayout.CENTER);
	}
	
	private JScrollPane createScrollPane(JComponent pComponent)
	{
		JScrollPane js = new JScrollPane(pComponent);
		js.setPreferredSize(new Dimension(300, 300));
		return js;
	}
	
	public class MoveTo implements ActionListener
	{
		public MoveTo(JList pSource, JList pDestination)
		{
			mSource = pSource;
			mDestination = pDestination;
		}
		
		public void actionPerformed(ActionEvent pEvt)
		{
			Object[] items = mSource.getSelectedValues();
			for ( Object o : items )
			{
				((DefaultListModel)mDestination.getModel()).add(searchIndex(o),o);
				((DefaultListModel)mSource.getModel()).removeElement(o);
			}
			mSource.clearSelection();
		}
		
		private int searchIndex(Object o)
		{
			for ( int i=0; i<mDestination.getModel().getSize(); ++i)
			{
				if ( o.toString().compareTo(mDestination.getModel().getElementAt(i).toString()) == -1 )
					return i;
			}
			return mDestination.getModel().getSize();
		}

		private JList mSource;
		private JList mDestination;
	}
	
	public class SelectionListener implements ListSelectionListener
	{
		public SelectionListener(JButton pTarget)
		{
			mTarget = pTarget;
		}
		
		public void valueChanged(ListSelectionEvent pEvt)
		{
			mTarget.setEnabled(((JList)pEvt.getSource()).getSelectedValues().length > 0);
		}
		
		private JButton mTarget;
	}

	private JList mAvailable;
	private JList mSelected;
}
