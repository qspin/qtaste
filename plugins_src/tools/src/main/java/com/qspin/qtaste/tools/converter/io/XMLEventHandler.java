package com.qspin.qtaste.tools.converter.io;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.qspin.qtaste.tools.converter.model.event.ActionEvent;
import com.qspin.qtaste.tools.converter.model.event.ChangeEvent;
import com.qspin.qtaste.tools.converter.model.event.DocumentEvent;
import com.qspin.qtaste.tools.converter.model.event.Event;
import com.qspin.qtaste.tools.converter.model.event.ItemEvent;
import com.qspin.qtaste.tools.converter.model.event.PropertyChangeEvent;
import com.qspin.qtaste.tools.converter.model.event.TreeSelectionEvent;

public class XMLEventHandler extends DefaultHandler {
	private List<Event> mDecodedEvent;
	private Event mEvent;

	private boolean inEvents;
	private boolean inEvent;
	private boolean inSource;
	private boolean inData;

	private StringBuffer mBuffer;

	public XMLEventHandler() {
		super();
		inData = false;
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (qName.equals(ROOT_ELEMENT)) {
			mDecodedEvent = new LinkedList<Event>();
			inEvents = true;
		} else if (qName.equals(EVENT_ELEMENT)) {
			mEvent = new Event();
			inEvent = true;
		} else if (qName.equals(SRC_ELEMENT)) {
			inSource = true;
		} else if (qName.equals(DATA_ELEMENT)) {
			inData = true;
		} else {
			if (qName.equals(CLASS_ELEMENT) || qName.equals(NAME_ELEMENT)
					||qName.equals(SELECTED_ITEM) || qName.equals(STATE_CHANGED)
					|| qName.equals(TYPE_ELEMENT) || qName.equals(TIME_ELEMENT)
					|| qName.equals(PROPERTY_NAME) || qName.equals(OLD_VALUE)
					|| qName.equals(NEW_VALUE) || qName.equals(LENGTH)
					|| qName.equals(OFFSET) || qName.equals(CHANGE)
					|| qName.equals(SELECTED_PATH) || qName.equals(NEW_TAB_INDEX)
					|| qName.equals(ID) || qName.equals(ACTION_COMMAND)) {
				mBuffer = new StringBuffer();
			}
		}
	}

	// detection fin de balise
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (qName.equals(ROOT_ELEMENT)) {
			inEvents = false;
		} else if (qName.equals(EVENT_ELEMENT)) {
			mDecodedEvent.add(mEvent);
			mEvent = null;
			inEvent = false;
		} else if (qName.equals(SRC_ELEMENT)) {
			inSource = false;
		} else if (qName.equals(DATA_ELEMENT)) {
			inData = false;
		} else if (qName.equals(NAME_ELEMENT)) {
			mEvent.setComponentName(mBuffer.toString());
			mBuffer = null;
		} else if (qName.equals(CLASS_ELEMENT)) {
			mEvent.setSourceClass(mBuffer.toString());
			mBuffer = null;
		} else if (qName.equals(TYPE_ELEMENT) && !inData) {
			mEvent.setType(mBuffer.toString());
			updateEvent();
			mBuffer = null;
		} else if (qName.equals(TIME_ELEMENT)) {
			mEvent.setTimeStamp(Long.parseLong(mBuffer.toString()));
			mBuffer = null;
		} else {
			if (inData) {
				if (mEvent instanceof PropertyChangeEvent) {
					fillPropertyChangeEvent((PropertyChangeEvent) mEvent, mBuffer.toString(), qName);
				} else if (mEvent instanceof DocumentEvent) {
					fillDocumentEvent((DocumentEvent) mEvent, mBuffer.toString(), qName);
				} else if (mEvent instanceof ActionEvent) {
					fillActionEvent((ActionEvent) mEvent, mBuffer.toString(), qName);
				} else if (mEvent instanceof ItemEvent) {
					fillItemEvent((ItemEvent) mEvent, mBuffer.toString(), qName);
				} else if (mEvent instanceof TreeSelectionEvent) {
					fillTreeExpansionEvent((TreeSelectionEvent) mEvent, mBuffer.toString(), qName);
				} else if (mEvent instanceof ChangeEvent) {
					fillChangeEvent((ChangeEvent) mEvent, mBuffer.toString(), qName);
				}
			} else {
				LOGGER.warn("Fin de balise " + qName + " inconnue.");
			}
		}
	}

	// detection de caracteres
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		String lecture = new String(ch, start, length);
		if (mBuffer != null)
			mBuffer.append(lecture);
	}

	// debut du parsing
	public void startDocument() throws SAXException {
		LOGGER.info("Debut du parsing");
	}

	// fin du parsing
	public void endDocument() throws SAXException {
		LOGGER.info("Fin du parsing");
	}

	private void updateEvent() {
		if (mEvent.getType().equals("PropertyChangeEvent")) {
			mEvent = new PropertyChangeEvent(mEvent);
		} else if (mEvent.getType().contains("DocumentEvent")) {
			mEvent = new DocumentEvent(mEvent);
			mEvent.setType("DocumentEvent");
		} else if (mEvent.getType().equals("ActionEvent")) {
			mEvent = new ActionEvent(mEvent);
		} else if (mEvent.getType().equals("ItemEvent")) {
			mEvent = new ItemEvent(mEvent);
		} else if (mEvent.getType().equals("TreeSelectionEvent")) {
			mEvent = new TreeSelectionEvent(mEvent);
		} else if (mEvent.getType().equals("ChangeEvent")) {
			mEvent = new ChangeEvent(mEvent);
		}
	}
	
	private void fillPropertyChangeEvent(PropertyChangeEvent pEvent, String pValue, String pName)
	{
		if (pName.equals(PROPERTY_NAME)) {
			pEvent.setPropertyName(pValue);
			mBuffer = null;
		} else if (pName.equals(OLD_VALUE)) {
			pEvent.setOldValue(pValue);
			mBuffer = null;
		} else if (pName.equals(NEW_VALUE)) {
			pEvent.setNewValue(pValue);
			mBuffer = null;
		}
	}
	
	private void fillActionEvent(ActionEvent pEvent, String pValue, String pName)
	{
		if (pName.equals(ID)) {
			pEvent.setId(pValue);
			mBuffer = null;
		} else if (pName.equals(ACTION_COMMAND)) {
			pEvent.setActionCommand(pValue);
			mBuffer = null;
		}
	}
	
	private void fillChangeEvent(ChangeEvent pEvent, String pValue, String pName)
	{
		if (pName.equals(NEW_TAB_INDEX)) {
			try {
				pEvent.setTabIndex(Integer.parseInt(pValue));
			}catch( NumberFormatException pExc)
			{
				LOGGER.warn("Invalid tab index.", pExc);
				pEvent.setTabIndex(0);
			}
			mBuffer = null;
		}
	}
	
	private void fillTreeExpansionEvent(TreeSelectionEvent pEvent, String pValue, String pName)
	{
		if (pName.equals(SELECTED_PATH)) {
			pEvent.setExpansionPath(pValue);
			mBuffer = null;
		}
	}

	private void fillDocumentEvent(DocumentEvent pEvent, String pValue, String pName)
	{
		if (pName.equals(TYPE_ELEMENT)) {
			pEvent.setDocumentChangeType(pValue);
			mBuffer = null;
		} else if (pName.equals(OFFSET)) {
			pEvent.setOffset(Integer.parseInt(pValue));
			mBuffer = null;
		} else if (pName.equals(LENGTH)) {
			pEvent.setLength(Integer.parseInt(pValue));
			mBuffer = null;
		} else if (pName.equals(CHANGE)) {
			pEvent.setChange(pValue);
			mBuffer = null;
		}
	}
	
	private void fillItemEvent(ItemEvent pEvent, String pValue, String pName)
	{
		if (pName.equals(ID)) {
			pEvent.setId(pValue);
			mBuffer = null;
		} else if (pName.equals(STATE_CHANGED)) {
			pEvent.setState(pValue);
			mBuffer = null;
		} else if (pName.equals(SELECTED_ITEM)) {
			pEvent.setSelectedItem(pValue);
			mBuffer = null;
		}
	}
	
	public List<Event> getDecodedEvent() {
		return mDecodedEvent;
	}

	private static final Logger LOGGER = Logger
			.getLogger(XMLEventHandler.class);
	private static final String ROOT_ELEMENT = "events";
	private static final String EVENT_ELEMENT = "event";
	private static final String SRC_ELEMENT = "source";
	private static final String NAME_ELEMENT = "name";
	private static final String CLASS_ELEMENT = "class";
	private static final String TYPE_ELEMENT = "type";
	private static final String DATA_ELEMENT = "data";
	private static final String TIME_ELEMENT = "time";

	// FOR PROPERTYCHANGE EVENT
	private static final String PROPERTY_NAME = "propertyName";
	private static final String OLD_VALUE = "oldValue";
	private static final String NEW_VALUE = "newValue";
	// FOR DOCUMENT EVENT
	private static final String OFFSET = "offset";
	private static final String LENGTH = "length";
	private static final String CHANGE = "change";
	// FOR ACTION EVENT
	private static final String ID = "id";
	private static final String ACTION_COMMAND = "actionCommand";
	// FOR ITEM EVENT
	//private static final String ID = "id";
	private static final String STATE_CHANGED = "stateChanged";
	private static final String SELECTED_ITEM = "selectedItem";
	//FOR TREE EXPANSION EVENT
	private static final String SELECTED_PATH = "selectedPath";
	//FOR TAB CHANGE IN TABBED PANE
	private static final String NEW_TAB_INDEX  = "newTabIndex";
}
