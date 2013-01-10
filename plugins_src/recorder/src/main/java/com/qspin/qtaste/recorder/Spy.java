/*
    Copyright 2007-2012 QSpin - www.qspin.be

    This file is part of QTaste framework.

    QTaste is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    QTaste is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with QTaste. If not, see <http://www.gnu.org/licenses/>.
*/

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
import java.util.ArrayList;
import java.util.Date;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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

import com.qspin.qtaste.tools.ComponentNamer;

/**
 * Reports action done on component through their listeners.
 * Components supported :<br/>
 * <ol>
 * <li>{@link AbstractButton}
 * <ul>
 * <li>{@link ActionListener}</li>
 * <li>{@link ItemListener}</li>
 * </ul>
 * </li>
 * <li>{@link JComboBox}
 * <ul>
 * <li>{@link ActionListener}</li>
 * <li>{@link ItemListener}</li>
 * </ul>
 * </li>
 * <li>{@link JList}
 * <ul>
 * <li>{@link ListSelectionListener}</li>
 * </ul>
 * </li>
 * <li>{@link JTable}
 * <ul>
 * <li>{@link ListSelectionListener}</li>
 * </ul>
 * </li>
 * <li>{@link JTree}
 * <ul>
 * <li>{@link TreeExpansionListener}</li>
 * <li>{@link TreeSelectionListener}</li>
 * </ul>
 * </li>
 * <li>{@link JTabbedPane}
 * <ul>
 * <li>{@link ChangeListener}</li>
 * </ul>
 * </li>
 * </ol>
 * 
 */
public class Spy implements DocumentListener, PropertyChangeListener,
		ItemListener, ActionListener, TreeSelectionListener,
		ListSelectionListener, TreeExpansionListener, ChangeListener {

	/**
	 * Constructor.
	 * 
	 * @param pWriter the writer to write the spy report.
	 */
	public Spy(BufferedWriter pWriter, List<RecorderFilter> pFilters)
	{
		mListeners = new ArrayList<PropertyChangeListener>();
		mActiveState = false;
		mWriter = pWriter;
		mFilters = pFilters;
		mListSelectionModelListenerMap = new HashMap<ListSelectionModel, String>();
		mTreeSelectionModelListenerMap = new HashMap<TreeSelectionModel, String>();
		mDocumentListenerMap = new HashMap<Document, String>();
		mEventIndex = 0;
	}

	/**
	 * Add the spy to the component.
	 * 
	 * @param pComponent
	 */
	public void addTarget(Component pComponent)
	{
		//if the spy is already active for the component, don't install it again.
		if ( pComponent.getListeners(Spy.class).length != 0 )
		{
			return;
		}
		addPropertyChangeListener(pComponent);
		if (pComponent instanceof AbstractButton) {
			addToAbstractButton((AbstractButton)pComponent);
		} else if (pComponent instanceof JComboBox) {
			addToJComboBox((JComboBox) pComponent);
		} else if (pComponent instanceof JTree) {
			addToJTree(((JTree) pComponent));
		} else if (pComponent instanceof JList) {
			addToJList((JList) pComponent);
		} else if (pComponent instanceof JTable) {
			addToJTable((JTable)pComponent);
		} else if (pComponent instanceof JTextComponent) {
			addToJTextComponent((JTextComponent)pComponent);
		} else if (pComponent instanceof JTabbedPane) {
			addToJTabbedPane((JTabbedPane)pComponent);
		} else {
			LOGGER.trace(" WARNING - Unsupported component : "
					+ pComponent.getClass());
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
			((JTextComponent) pComponent).getDocument().removeDocumentListener(this);
			mDocumentListenerMap.remove(((JTextComponent) pComponent).getDocument());
		} else if (pComponent instanceof JTabbedPane) {
			((JTabbedPane) pComponent).removeChangeListener(this);
		}
	}

	public void addPropertyChangeListener(PropertyChangeListener pListener) {
		mListeners.add(pListener);
	}
	public void removePropertyChangeListener(PropertyChangeListener pListener) {
		mListeners.remove(pListener);
	}
	
	public void setActive(boolean pActive)
	{
		mActiveState = pActive;
		PropertyChangeEvent evt = new PropertyChangeEvent(this, ACTIVE_STATE_PROPERTY, !mActiveState, mActiveState);
		firePropertyChange(evt);
	}
	
	public boolean isActive() {
		return mActiveState;
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
	@Override
	public void stateChanged(ChangeEvent e) {
		writeEvent(e);
	}
	
	protected boolean checkFilters(Component pComponent, Class<?> pListenerClass)
	{
		List<Object> data = new ArrayList<Object>();
		data.add(pComponent);
		data.add(pListenerClass);

		for ( RecorderFilter filter : mFilters )
		{
			if (!filter.accept(data) )
			{
				LOGGER.debug(pComponent.getName() + " is not spied with " + pListenerClass);
				return false;
			}
		}
		return true;
	}
	
	protected void firePropertyChange(PropertyChangeEvent pEvt)
	{
		for (PropertyChangeListener l : mListeners)
		{
			l.propertyChange(pEvt);
		}
	}
	
	protected void addPropertyChangeListener(Component pComponent)
	{
		if ( checkFilters(pComponent, PropertyChangeListener.class))
		{
			pComponent.addPropertyChangeListener(this);
		}
	}
	protected void addToAbstractButton(AbstractButton pComponent)
	{
		if ( checkFilters(pComponent, ActionListener.class))
		{
			pComponent.addActionListener(this);
		}
		if ( checkFilters(pComponent, ItemListener.class))
		{
			pComponent.addItemListener(this);
		}
	}
	protected void addToJComboBox(JComboBox pComponent)
	{
		if ( checkFilters(pComponent, ActionListener.class))
		{
			pComponent.addActionListener(this);
		}
		if ( checkFilters(pComponent, ItemListener.class))
		{
			pComponent.addItemListener(this);
		}
	}
	protected void addToJTree(JTree pComponent)
	{
		if( checkFilters(pComponent, TreeSelectionListener.class))
		{
			pComponent.getSelectionModel().addTreeSelectionListener(this);
			mTreeSelectionModelListenerMap.put(
					((JTree) pComponent).getSelectionModel(),
					ComponentNamer.getInstance().getNameForComponent(pComponent));
		}
		if ( checkFilters(pComponent, TreeExpansionListener.class))
		{
			((JTree) pComponent).addTreeExpansionListener(this);
		}
	}
	protected void addToJList(JList pComponent)
	{
		if( checkFilters(pComponent, ListSelectionListener.class))
		{
			pComponent.getSelectionModel().addListSelectionListener(this);
			mListSelectionModelListenerMap.put( pComponent.getSelectionModel(),
					ComponentNamer.getInstance().getNameForComponent(pComponent));
		}
	}
	protected void addToJTable(JTable pComponent)
	{
		if ( checkFilters(pComponent, ListSelectionListener.class))
		{
			pComponent.getSelectionModel().addListSelectionListener(this);
			mListSelectionModelListenerMap.put(pComponent.getSelectionModel(),
					ComponentNamer.getInstance().getNameForComponent(pComponent));
		}
	}
	protected void addToJTextComponent(JTextComponent pComponent)
	{
		if ( checkFilters(pComponent, DocumentListener.class))
		{
			pComponent.getDocument().addDocumentListener(this);
			mDocumentListenerMap.put( pComponent.getDocument(),
					ComponentNamer.getInstance().getNameForComponent(pComponent));
		}
	}
	protected void addToJTabbedPane(JTabbedPane pComponent)
	{
		if ( checkFilters(pComponent, ChangeListener.class))
		{
			pComponent.addChangeListener(this);
		}
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
			} else if (source instanceof Component) {
				name = ComponentNamer.getInstance().getNameForComponent((Component) source);
			}
			if (name == null) {
				LOGGER.trace(" WARNING - Unable to find the component source");
				return;
			}
			builder.append("<time>" + new Date().getTime() + "</time>" + LINE_BREAK);
			builder.append("<name>" + name.replaceAll("&", "&#38;") + "</name>" + LINE_BREAK);
			builder.append("<class>" + source.getClass().getName() + "</class>" + LINE_BREAK);
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
			}else if (pEvent instanceof ChangeEvent) {
				readEventData((ChangeEvent) pEvent, builder);
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
			if (name == null) {
				LOGGER.trace(" WARNING - Unable to find the component source");
				return;
			}
			builder.append("<time>" + new Date().getTime() + "</time>" + LINE_BREAK);
			builder.append("<name>" + name.replaceAll("&", "&#38;") + "</name>" + LINE_BREAK);
			builder.append("<class>" + source.getClass().getName() + "</class>" + LINE_BREAK);
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
			if (pEvent.getType() != EventType.REMOVE)
			{
				builder.append(pEvent.getDocument().getText(0, pEvent.getDocument().getLength()));
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

	private void readEventData(ChangeEvent pEvent, StringBuilder pBuilder) {
		if ( pEvent.getSource() instanceof JTabbedPane )
		{
			pBuilder.append("<newTabIndex>" + ((JTabbedPane)pEvent.getSource()).getSelectedIndex() + "</newTabIndex>" + LINE_BREAK);
		}
	}

	private void readEventData(ItemEvent pEvent, StringBuilder pBuilder) {
		pBuilder.append("<id>" + pEvent.getID() + "</id>" + LINE_BREAK);
		pBuilder.append("<stateChanged>");
		if (pEvent.getStateChange() == ItemEvent.SELECTED)
			pBuilder.append("SELECTED");
		else
			pBuilder.append("DESELECTED");
		pBuilder.append("</stateChanged>" + LINE_BREAK);
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

	protected List<PropertyChangeListener> mListeners;
	protected boolean mActiveState;
	protected BufferedWriter mWriter;
	protected List<RecorderFilter> mFilters;
	protected Map<ListSelectionModel, String> mListSelectionModelListenerMap;
	protected Map<TreeSelectionModel, String> mTreeSelectionModelListenerMap;
	protected Map<Document, String> mDocumentListenerMap;
	protected int mEventIndex;

	protected static final Logger LOGGER = Logger.getLogger(Spy.class);
	private static final String LINE_BREAK = System.getProperty("line.separator");
	
	public static final String ACTIVE_STATE_PROPERTY = "active.state";
}