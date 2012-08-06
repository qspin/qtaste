package com.qspin.qtaste.recorder;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Date;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentEvent.EventType;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
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
public class Spy implements DocumentListener, PropertyChangeListener, ItemListener, ActionListener, TreeSelectionListener, ListSelectionListener, TreeExpansionListener {

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
		mDocumentListenerMap = new HashMap<Document, String>();
		mEventIndex = 0;
	}

	/**
	 * Add the spy to the component.
	 * @param pComponent
	 */
	public void addTarget(Component pComponent)
	{
		pComponent.addPropertyChangeListener(this);
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
		} else if (pComponent instanceof JTextComponent) {
			((JTextComponent)pComponent).getDocument().addDocumentListener(this);
			mDocumentListenerMap.put(((JTextComponent) pComponent).getDocument(), pComponent.getName());
			LOGGER.trace("component name : " + pComponent.getName() );
		} else {
			LOGGER.trace(" WARNING - Unsupported component : " + pComponent.getClass() );
			return;
		}
		
	}

	public void removeTarget(Component pComponent) {
		pComponent.removePropertyChangeListener(this);
		if (pComponent instanceof AbstractButton) {
			((AbstractButton) pComponent).removeActionListener(this);
			((AbstractButton) pComponent).removeItemListener(this);
		} else if (pComponent instanceof JComboBox) {
			((JComboBox) pComponent).removeActionListener(this);
			((JComboBox) pComponent).removeItemListener(this);
		} else if (pComponent instanceof JTree) {
			((JTree) pComponent).getSelectionModel().removeTreeSelectionListener(this);
			((JTree) pComponent).removeTreeExpansionListener(this);
			mTreeSelectionModelListenerMap.remove(((JTree) pComponent).getSelectionModel());
		} else if (pComponent instanceof JList) {
			((JList) pComponent).getSelectionModel().removeListSelectionListener(this);
			mListSelectionModelListenerMap.remove(((JList) pComponent).getSelectionModel());
		} else if (pComponent instanceof JTable) {
			((JTable) pComponent).getSelectionModel().removeListSelectionListener(this);
			mListSelectionModelListenerMap.remove(((JTable) pComponent).getSelectionModel());
		} else if (pComponent instanceof JTextComponent) {
			((JTextComponent)pComponent).getDocument().removeDocumentListener(this);
			mDocumentListenerMap.remove(((JTextComponent) pComponent).getDocument());
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
	@Override
	public void insertUpdate(DocumentEvent e) {
		writeEvent(e);
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		writeEvent(e);
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		writeEvent(e);
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		writeEvent(e);
	}

	protected synchronized void writeEvent(EventObject pEvent, Object... pOther) {
		try {
			StringBuilder builder = new StringBuilder();
			builder.append("<event>" + LINE_BREAK);
			Object source = pEvent.getSource();
			builder.append("<source>" + LINE_BREAK);
			String name = null;
			if (mListSelectionModelListenerMap.containsKey(source)) {
				name = mListSelectionModelListenerMap.get(source);
			} else if (mTreeSelectionModelListenerMap.containsKey(source)) {
				name = mTreeSelectionModelListenerMap.get(source);
			} else if ( source instanceof Component ){
				name = ((Component)source).getName();
			} 
			if ( name == null ){
				LOGGER.trace(" WARNING - Unable to find the component source");
				return;
			}
			builder.append("<time>" + new Date().getTime() + "</time>" + LINE_BREAK);
			builder.append("<name>" + name.replaceAll("&", "&#38;") + "</name>" + LINE_BREAK);
			builder.append("<class>" + source.getClass().getName() + "</class>"+ LINE_BREAK);
			String eventClassName = pEvent.getClass().getName();
			if (eventClassName.indexOf(".") > 0) {
				eventClassName = eventClassName.substring(eventClassName
						.lastIndexOf(".") + 1);
			}
			builder.append("</source>" + LINE_BREAK);
			builder.append("<type>" + eventClassName + "</type>" + LINE_BREAK);
			builder.append("<data>" + LINE_BREAK);
			if (pEvent instanceof ActionEvent) {
				readEventData((ActionEvent) pEvent, builder);
			} else if (pEvent instanceof ItemEvent) {
				readEventData((ItemEvent) pEvent, builder);
			} else if (pEvent instanceof TreeSelectionEvent) {
				readEventData((TreeSelectionEvent) pEvent, builder);
			} else if (pEvent instanceof ListSelectionEvent) {
				readEventData((ListSelectionEvent) pEvent, builder);
			} else if (pEvent instanceof TreeExpansionEvent) {
				builder.append("<expansion>" + pOther[0] + "</expansion>" + LINE_BREAK);
				readEventData((TreeExpansionEvent) pEvent, builder);
			} else if (pEvent instanceof PropertyChangeEvent) {
				readEventData((PropertyChangeEvent) pEvent, builder);
			}
			builder.append("</data>" + LINE_BREAK);
			builder.append("</event>" + LINE_BREAK);

			mWriter.write(builder.toString());
			mWriter.newLine();
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
	
	protected synchronized void writeEvent(DocumentEvent pEvent)
	{
		StringBuilder builder = new StringBuilder();
		try {
			builder.append("<event>" + LINE_BREAK);
			Object source = pEvent.getDocument();
			builder.append("<source>" + LINE_BREAK);
			String name = null;
			if (mDocumentListenerMap.containsKey(source)) {
				name = mDocumentListenerMap.get(source);
			} 
			if ( name == null ){
				LOGGER.trace(" WARNING - Unable to find the component source");
				return;
			}
			builder.append("<time>" + new Date().getTime() + "</time>" + LINE_BREAK);
			builder.append("<name>" + name.replaceAll("&", "&#38;") + "</name>" + LINE_BREAK);
			builder.append("<class>" + source.getClass().getName() + "</class>"+ LINE_BREAK);
			String eventClassName = pEvent.getClass().getName();
			if (eventClassName.indexOf(".") > 0) {
				eventClassName = eventClassName.substring(eventClassName.lastIndexOf(".") + 1);
			}
			builder.append("</source>" + LINE_BREAK);
			builder.append("<type>DocumentEvent	</type>" + LINE_BREAK);
			builder.append("<data>" + LINE_BREAK);
			builder.append("<type>" + pEvent.getType() + "</type>" + LINE_BREAK);
			builder.append("<offset>" + pEvent.getOffset() + "</offset>" + LINE_BREAK);
			builder.append("<length>" + pEvent.getLength() + "</length>" + LINE_BREAK);
			builder.append("<change>");
			if ( pEvent.getType() != EventType.REMOVE )
			{
				builder.append(pEvent.getDocument().getText(pEvent.getOffset(), pEvent.getLength())); 
			}
			builder.append("</change>" + LINE_BREAK);
			builder.append("</data>" + LINE_BREAK);
			builder.append("</event>" + LINE_BREAK);

			mWriter.write(builder.toString());
			mWriter.newLine();
		} catch (BadLocationException pExc) {
			pExc.printStackTrace();
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

	private void readEventData(ActionEvent pEvent, StringBuilder pBuilder) {
		pBuilder.append("<id>" + pEvent.getID() + "</id>" + LINE_BREAK);
		pBuilder.append("<actionCommand>" + pEvent.getActionCommand() + "</actionCommand>" + LINE_BREAK);
	}

	private void readEventData(ItemEvent pEvent, StringBuilder pBuilder) {
		pBuilder.append("<id>" + pEvent.getID() + "</id>" + LINE_BREAK);
		pBuilder.append("<stateChanged>");
		if (pEvent.getStateChange() == ItemEvent.SELECTED)
			pBuilder.append("SELECTED");
		else
			pBuilder.append("DESELECTED");
		pBuilder.append("</stateChanged>" + LINE_BREAK );
		pBuilder.append("<selectedItem>" + pEvent.getItem() + "</selectedItem>" + LINE_BREAK);
	}

	private void readEventData(TreeSelectionEvent pEvent, StringBuilder pBuilder) {
		pBuilder.append("<selectedPath>" + pEvent.getNewLeadSelectionPath() + "</selectedPath>" + LINE_BREAK);
	}

	private void readEventData(ListSelectionEvent pEvent, StringBuilder pBuilder) {
		pBuilder.append("<firstIndex>" + pEvent.getFirstIndex() + "</firstIndex>" + LINE_BREAK);
		pBuilder.append("<lastIndex>" + pEvent.getLastIndex() + "</lastIndex>" + LINE_BREAK);
		pBuilder.append("<valueAjusting>" + pEvent.getValueIsAdjusting() + "</valueAjusting>" + LINE_BREAK);
	}

	private void readEventData(TreeExpansionEvent pEvent, StringBuilder pBuilder) {
		pBuilder.append("<expansionPath>" + pEvent.getPath() + "</expansionPath>" + LINE_BREAK);
	}

	private void readEventData(PropertyChangeEvent pEvent, StringBuilder pBuilder) {
		pBuilder.append("<propertyName>" + pEvent.getPropertyName() + "</propertyName>" + LINE_BREAK);
		pBuilder.append("<oldValue>" + pEvent.getOldValue() + "</oldValue>" + LINE_BREAK);
		pBuilder.append("<newValue>" + pEvent.getNewValue() + "</newValue>" + LINE_BREAK);
	}

	protected BufferedWriter mWriter;
	protected Map<ListSelectionModel, String> mListSelectionModelListenerMap;
	protected Map<TreeSelectionModel, String> mTreeSelectionModelListenerMap;
	protected Map<Document, String> mDocumentListenerMap;
	protected int mEventIndex;
	
	protected static final Logger LOGGER = Logger.getLogger(Spy.class);
	private static final String LINE_BREAK = System.getProperty("line.separator");
}