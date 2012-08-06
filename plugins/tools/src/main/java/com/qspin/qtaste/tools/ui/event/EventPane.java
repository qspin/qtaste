package com.qspin.qtaste.tools.ui.event;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.qspin.qtaste.tools.model.EventManager;
import com.qspin.qtaste.tools.model.event.DocumentEvent;
import com.qspin.qtaste.tools.model.event.Event;
import com.qspin.qtaste.tools.model.event.ItemEvent;
import com.qspin.qtaste.tools.model.event.PropertyChangeEvent;
import com.qspin.qtaste.tools.model.node.ComponentNode;
import com.qspin.qtaste.tools.model.node.EventNode;
import com.qspin.qtaste.tools.model.node.EventTypeNode;

public class EventPane extends JPanel implements TreeSelectionListener {

	public EventPane() {
		mEventPanes = new HashMap<Class<? extends Event>, AbstractSpecificEventPane>();
		mEventPanes.put(PropertyChangeEvent.class,
				new PropertyChangeEventPane());
		mEventPanes.put(com.qspin.qtaste.tools.model.event.ActionEvent.class,
				new ActionEventPane());
		mEventPanes.put(ItemEvent.class, new ItemEventPane());
		mEventPanes.put(DocumentEvent.class, new DocumentEventPane());
		genUI();
		setBorder(BorderFactory.createTitledBorder("Event"));
	}

	private void genUI() {
		setLayout(new BorderLayout());
		FormLayout layout = new FormLayout("right:pref, 3dlu, pref:grow",
				"pref, 3dlu, pref, 3dlu, pref, 3dlu, pref");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		int rowIndex = 1;

		builder.addLabel("Event type : ", cc.xy(1, rowIndex));
		mEventType = new JLabel();
		builder.add(mEventType, cc.xy(3, rowIndex));
		rowIndex += 2;

		builder.addLabel("Timestamp : ", cc.xy(1, rowIndex));
		mTimestamp = new JLabel();
		builder.add(mTimestamp, cc.xy(3, rowIndex));
		rowIndex += 2;

		builder.add(createSourcePanel(), cc.xyw(1, rowIndex, 3));
		rowIndex += 2;

		mExtention = new JPanel();
		builder.add(mExtention, cc.xyw(1, rowIndex, 3));

		add(builder.getPanel());
		resetFields();
	}

	private JPanel createSourcePanel() {
		FormLayout layout = new FormLayout(
				"right:pref, 3dlu, pref:grow, 3dlu, pref:grow",
				"pref, 3dlu, pref, 3dlu, pref");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.setBorder(BorderFactory.createTitledBorder("Source"));
		int rowIndex = 1;

		builder.addLabel("Component name : ", cc.xy(1, rowIndex));
		mComponentName = new JLabel();
		builder.add(mComponentName, cc.xy(3, rowIndex));
		rowIndex += 2;

		builder.addLabel("Alias : ", cc.xy(1, rowIndex));
		mComponentAlias = new JLabel();
		builder.add(mComponentAlias, cc.xy(3, rowIndex));
		rowIndex += 2;

		builder.addLabel("Component class : ", cc.xy(1, rowIndex));
		mComponentClass = new JLabel();
		builder.add(mComponentClass, cc.xy(3, rowIndex));
		rowIndex += 2;

		return builder.getPanel();
	}

	private void resetFields() {
		mComponentName.setText(null);
		mComponentClass.setText(null);
		mComponentAlias.setText(null);
		mEventType.setText(null);
		mTimestamp.setText(null);
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		mCurrentSelectedPath = e.getPath();
		MutableTreeNode selectedNode = (MutableTreeNode) mCurrentSelectedPath.getLastPathComponent();
		resetFields();
		changeExtention(null);
		if (selectedNode instanceof ComponentNode) {
			mComponentName.setText(((ComponentNode) selectedNode).getUserObject().toString());
			mComponentClass.setText(((ComponentNode) selectedNode).getComponentClass());
		} else if (selectedNode instanceof EventTypeNode) {
			mEventType.setText(((EventTypeNode) selectedNode).getUserObject().toString());
		} else if (selectedNode instanceof EventNode) {
			Event event = ((EventNode) selectedNode).getEvent();
			mComponentName.setText(event.getComponentName());
			mComponentClass.setText(event.getSourceClass());
			mComponentAlias.setText(event.getAlias());
			mEventType.setText(event.getType());
			mTimestamp.setText(Long.toString(event.getTimeStamp()));
			changeExtention(getSpecificEventPane(event));
		}
	}

	private AbstractSpecificEventPane getSpecificEventPane(Event pEvent) {
		if (mEventPanes.containsKey(pEvent.getClass())) {
			AbstractSpecificEventPane pane = mEventPanes.get(pEvent.getClass());
			pane.resetFor(pEvent);
			return pane;
		}
		return null;
	}

	private void changeExtention(AbstractSpecificEventPane pPanel) {
		mExtention.removeAll();
		mExtention.setLayout(new BorderLayout());
		mExtention.setVisible(pPanel != null);
		if (pPanel != null) {
			mExtention.add(pPanel, BorderLayout.CENTER);
			mExtention.invalidate();
		}
	}

	private JLabel mComponentName;
	private JLabel mComponentClass;
	private JLabel mEventType;
	private JLabel mComponentAlias;
	private JLabel mTimestamp;
	private JPanel mExtention;
	private Map<Class<? extends Event>, AbstractSpecificEventPane> mEventPanes;
	private TreePath mCurrentSelectedPath;
}
