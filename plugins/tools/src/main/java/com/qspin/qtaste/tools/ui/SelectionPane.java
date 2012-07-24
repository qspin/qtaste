package com.qspin.qtaste.tools.ui;

import static com.qspin.qtaste.tools.ui.UIConstants.COMPONENT_SPACING;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

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
		DefaultListModel<Object> model = ((DefaultListModel<Object>)mAvailable.getModel());
		model.clear();
		for ( Object o : pAvailableValues )
		{
			model.add(model.size(), o);
		}
		((DefaultListModel<Object>)mSelected.getModel()).clear();
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

		mAvailable = new JList<Object>(new DefaultListModel<Object>());
		builder.add(createScrollPane(mAvailable), cc.xywh(1,3,1,5));
		
		mSelected = new JList<Object>(new DefaultListModel<Object>());
		builder.add(createScrollPane(mSelected), cc.xywh(5,3,1,5));

		JButton toRight = new JButton("=>");
		builder.add(toRight, cc.xy(3,4));
		JButton toLeft = new JButton("<=");
		builder.add(toLeft, cc.xy(3,6));
		
		setLayout(new BorderLayout());
		add(builder.getPanel(), BorderLayout.CENTER);
	}
	
	private JScrollPane createScrollPane(JComponent pComponent)
	{
		JScrollPane js = new JScrollPane(pComponent);
		js.setPreferredSize(new Dimension(300, 300));
		return js;
	}

	private JList<Object> mAvailable;
	private JList<Object> mSelected;
}
