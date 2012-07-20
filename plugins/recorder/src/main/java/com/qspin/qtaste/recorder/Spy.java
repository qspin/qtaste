package com.qspin.qtaste.recorder;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;

/**
 * Reports action done on component through their listeners.
 * Components supported :<br/>
 * <ol>
 * <li>{@link AbstractButton}<ul><li>{@link ActionListener}</li><li>{@link ItemListener}</li></ul></li>
 * <li>{@link JComboBox}<ul><li>{@link ActionListener}</li><li>{@link ItemListener}</li></ul></li>
 * <li>{@link JList}<ul><li>{@link ListSelectionListener}</li></ul></li>
 * <li>{@link JTable}<ul><li>{@link ListSelectionListener}</li></ul></li>
 * <li>{@link JTree}<ul><li>{@link TreeExpansionListener}</li><li>{@link TreeSelectionListener}</li></ul></li>
 * </ol>
 * 
 */
public class Spy implements ItemListener, ActionListener, TreeSelectionListener, ListSelectionListener, TreeExpansionListener {

	/**
	 * Constructor.
	 * 
	 * @param pWriter the writer to write the spy report.
	 */
	public Spy(BufferedWriter pWriter)
	{
		mWriter = pWriter;
		mListSelectionModelListenerMap = new HashMap<ListSelectionModel, String>();
		mTreeSelectionModelListenerMap = new HashMap<TreeSelectionModel, String>();
		mEventIndex = 0;
	}

	/**
	 * Add the spy to the component.
	 * @param pComponent
	 */
	public void addTarget(Component pComponent)
	{
		if (pComponent instanceof AbstractButton) {
			((AbstractButton) pComponent).addActionListener(this);
			((AbstractButton) pComponent).addItemListener(this);
		} else if (pComponent instanceof JComboBox) {
			((JComboBox) pComponent).addActionListener(this);
			((JComboBox) pComponent).addItemListener(this);
		} else if (pComponent instanceof JTree) {
			((JTree) pComponent).getSelectionModel().addTreeSelectionListener(this);
			((JTree) pComponent).addTreeExpansionListener(this);
			mTreeSelectionModelListenerMap.put(((JTree) pComponent).getSelectionModel(), pComponent.getName());
		} else if (pComponent instanceof JList) {
			((JList) pComponent).getSelectionModel().addListSelectionListener(this);
			mListSelectionModelListenerMap.put(((JList) pComponent).getSelectionModel(), pComponent.getName());
		} else if (pComponent instanceof JTable) {
			((JTable) pComponent).getSelectionModel().addListSelectionListener(this);
			mListSelectionModelListenerMap.put(((JTable) pComponent).getSelectionModel(), pComponent.getName());
		} else {
			LOGGER.warn("Unsupported component : " + pComponent.getClass() );
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		writeEvent(e);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		writeEvent(e);
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		writeEvent(e);
	}
	@Override
	public void valueChanged(ListSelectionEvent e) {
		writeEvent(e);
	}
	@Override
	public void treeExpanded(TreeExpansionEvent e) {
		writeEvent(e, true);
	}

	@Override
	public void treeCollapsed(TreeExpansionEvent e) {
		writeEvent(e, false);
	}

	protected synchronized void writeEvent(EventObject pEvent, Object... pOther) {
		try {
			StringBuilder builder = new StringBuilder();
			String prefix = "event." + mEventIndex + ".";
			Object source = pEvent.getSource();
			if (mListSelectionModelListenerMap.containsKey(source)) {
				builder.append( prefix + "source = " + mListSelectionModelListenerMap.get(source) + LINE_BREAK);
			} else if (mTreeSelectionModelListenerMap.containsKey(source)) {
				builder.append( prefix + "source = " + mTreeSelectionModelListenerMap.get(source) + LINE_BREAK);
			} else if ( source instanceof Component ){
				builder.append( prefix + "source = " + ((Component)source).getName() + LINE_BREAK);
			} else {
				LOGGER.warn("Unable to find the component source");
				return;
			}
			builder.append( prefix + "class = " + source.getClass().getName() + LINE_BREAK);
			String eventClassName = pEvent.getClass().getName();
			if (eventClassName.indexOf(".") > 0) {
				eventClassName = eventClassName.substring(eventClassName
						.lastIndexOf(".") + 1);
			}
			builder.append(prefix + "type = " + eventClassName + LINE_BREAK);
			prefix += "data.";
			if (pEvent instanceof ActionEvent) {
				readEventData((ActionEvent) pEvent, builder, prefix);
			} else if (pEvent instanceof ItemEvent) {
				readEventData((ItemEvent) pEvent, builder, prefix);
			} else if (pEvent instanceof TreeSelectionEvent) {
				readEventData((TreeSelectionEvent) pEvent, builder, prefix);
			} else if (pEvent instanceof ListSelectionEvent) {
				readEventData((ListSelectionEvent) pEvent, builder, prefix);
			} else if (pEvent instanceof TreeExpansionEvent) {
				builder.append(prefix + "expansion : " + pOther[0] + LINE_BREAK);
				readEventData((TreeExpansionEvent) pEvent, builder, prefix);
			}

			mWriter.write(builder.toString());
			mWriter.newLine();
			LOGGER.info(builder.toString());
		} catch (IOException pExc) {
			pExc.printStackTrace();
		} finally {
			++mEventIndex;
			try {
				mWriter.flush();
			} catch (IOException e) {
				// e.printStackTrace();
			}
		}
	}

	private void readEventData(ActionEvent pEvent, StringBuilder pBuilder, String pPrefix) {
		pBuilder.append(pPrefix + "id = " + pEvent.getID() + LINE_BREAK);
		pBuilder.append(pPrefix + "actionCommand = "
				+ pEvent.getActionCommand() + LINE_BREAK);
	}

	private void readEventData(ItemEvent pEvent, StringBuilder pBuilder, String pPrefix) {
		pBuilder.append(pPrefix + "id = " + pEvent.getID() + LINE_BREAK);
		pBuilder.append(pPrefix + "stateChanged = ");
		if (pEvent.getStateChange() == ItemEvent.SELECTED)
			pBuilder.append("SELECTED" + LINE_BREAK);
		else
			pBuilder.append("DESELECTED" + LINE_BREAK);

		pBuilder.append(pPrefix + "selectedItem = " + pEvent.getItem() + LINE_BREAK);
	}

	private void readEventData(TreeSelectionEvent pEvent, StringBuilder pBuilder, String pPrefix) {
		pBuilder.append(pPrefix + "selectedPath = " + pEvent.getNewLeadSelectionPath() + LINE_BREAK);
	}

	private void readEventData(ListSelectionEvent pEvent, StringBuilder pBuilder, String pPrefix) {
		pBuilder.append(pPrefix + "firstIndex = " + pEvent.getFirstIndex() + LINE_BREAK);
		pBuilder.append(pPrefix + "lastIndex = " + pEvent.getLastIndex() + LINE_BREAK);
		pBuilder.append(pPrefix + "valueAjusting = " + pEvent.getValueIsAdjusting() + LINE_BREAK);
	}

	private void readEventData(TreeExpansionEvent pEvent, StringBuilder pBuilder, String pPrefix) {
		pBuilder.append(pPrefix + "expansionPath = " + pEvent.getPath() + LINE_BREAK);
	}

	protected BufferedWriter mWriter;
	protected Map<ListSelectionModel, String> mListSelectionModelListenerMap;
	protected Map<TreeSelectionModel, String> mTreeSelectionModelListenerMap;
	protected int mEventIndex;
	
	protected static final Logger LOGGER = Logger.getLogger(Spy.class);
	private static final String LINE_BREAK = System.getProperty("line.separator");
}