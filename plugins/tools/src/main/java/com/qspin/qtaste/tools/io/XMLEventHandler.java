package com.qspin.qtaste.tools.io;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.qspin.qtaste.tools.model.DocumentEvent;
import com.qspin.qtaste.tools.model.Event;
import com.qspin.qtaste.tools.model.PropertyChangeEvent;

public class XMLEventHandler extends DefaultHandler{
		//résultats de notre parsing
		private List<Event> mDecodedEvent;
		private Event mEvent;
		//flags nous indiquant la position du parseur
		private boolean inEvents;
		 private boolean inEvent;
		  private boolean inSource;
			private boolean inName;
			private boolean inClass;
		  private boolean inData;
		    //PropertyChangeEvent
		  	private boolean inPropertyName;
		  	private boolean inOldValue;
		  	private boolean inNewValue;
		  private boolean inType;
		  private boolean inTime;
		//buffer nous permettant de récupérer les données 
		private StringBuffer mBuffer;

		// simple constructeur
		public XMLEventHandler(){
			super();
		}
		
		//détection d'ouverture de balise
		public void startElement(String uri, String localName,
				String qName, Attributes attributes) throws SAXException{
			if(qName.equals(ROOT_ELEMENT)){
				mDecodedEvent = new LinkedList<Event>();
				inEvents = true;
			}else if(qName.equals(EVENT_ELEMENT)){
				mEvent = new Event();
				inEvent = true;
			}else if(qName.equals(SRC_ELEMENT)){
				inSource = true;
			}else if(qName.equals(DATA_ELEMENT)){
				inData = true;
			}else {
				mBuffer = new StringBuffer();
//				if(qName.equals(CLASS_ELEMENT)){
//					inClass = true;
//				}else if(qName.equals(NAME_ELEMENT)){
//					inName = true;
//				}else 
				if(qName.equals(TYPE_ELEMENT) && !inData){
					inType = true;
//				}else if(qName.equals(TIME_ELEMENT)){
//					inTime = true;
//				}else if(qName.equals(PROPERTY_NAME)){
//					inPropertyName = true;
//				}else if(qName.equals(NEW_VALUE)){
//					inNewValue = true;
//				}else if(qName.equals(OLD_VALUE)){
//					inOldValue = true;
//				}else{
//					//erreur, on peut lever une exception
//					LOGGER.warn("Debut de balise "+qName+" inconnue.");
				}
			}
		}
		//détection fin de balise
		public void endElement(String uri, String localName, String qName)
				throws SAXException{
			if(qName.equals(ROOT_ELEMENT)){
				inEvents = false;
			}else if(qName.equals(EVENT_ELEMENT)){
				mDecodedEvent.add(mEvent);
				mEvent = null;
				inEvent = false;
			}else if(qName.equals(SRC_ELEMENT)){
				inSource = false;
			}else if(qName.equals(DATA_ELEMENT)){
				inSource = false;
			}else if(qName.equals(NAME_ELEMENT)){
				mEvent.setComponentName(mBuffer.toString());
				mBuffer = null;
				inName = false;
			}else if(qName.equals(CLASS_ELEMENT)){
				mEvent.setSourceClass(mBuffer.toString());
				mBuffer = null;
				inType = false;
			}else if(qName.equals(TYPE_ELEMENT) && !inData){
				mEvent.setType(mBuffer.toString());
				updateEvent();
				mBuffer = null;
				inType = false;
			}else if(qName.equals(TIME_ELEMENT)){
				mEvent.setTimeStamp(Long.parseLong(mBuffer.toString()));
				mBuffer = null;
				inType = false;
			}else{
				if ( inData ) {
					if ( mEvent instanceof PropertyChangeEvent )
					{
						if(qName.equals(PROPERTY_NAME)){
							((PropertyChangeEvent)mEvent).setPropertyName(mBuffer.toString());
							mBuffer = null;
							inPropertyName = false;
						} else if(qName.equals(OLD_VALUE)){
							((PropertyChangeEvent)mEvent).setOldValue(mBuffer.toString());
							mBuffer = null;
							inOldValue = false;
						} else if(qName.equals(NEW_VALUE)){
							((PropertyChangeEvent)mEvent).setNewValue(mBuffer.toString());
							mBuffer = null;
							inNewValue = false;
						}  else {
							//erreur, on peut lever une exception
							LOGGER.warn("Fin de balise "+qName+" inconnue.");
						}
					} else if ( mEvent instanceof DocumentEvent ) {

						if(qName.equals(TYPE_ELEMENT)){
							((DocumentEvent)mEvent).setDocumentChangeType(mBuffer.toString());
							mBuffer = null;
							inPropertyName = false;
						} else if(qName.equals(OFFSET)){
							((DocumentEvent)mEvent).setOffset(Integer.parseInt(mBuffer.toString()));
							mBuffer = null;
							inOldValue = false;
						} else if(qName.equals(LENGTH)){
							((DocumentEvent)mEvent).setLength(Integer.parseInt(mBuffer.toString()));
							mBuffer = null;
							inNewValue = false;
						} else if(qName.equals(CHANGE)){
							((DocumentEvent)mEvent).setChange(mBuffer.toString());
							mBuffer = null;
							inNewValue = false;
						} else {
							//erreur, on peut lever une exception
							LOGGER.warn("Fin de balise "+qName+" inconnue.");
						}
					}
				} else {
					//erreur, on peut lever une exception
					LOGGER.warn("Fin de balise "+qName+" inconnue.");
				}
			}          
		}
		
		//détection de caractères
		public void characters(char[] ch,int start, int length)
				throws SAXException{
			String lecture = new String(ch,start,length);
			if(mBuffer != null) mBuffer.append(lecture);       
		}
		
		//début du parsing
		public void startDocument() throws SAXException {
			LOGGER.info("Début du parsing");
		}
		
		//fin du parsing
		public void endDocument() throws SAXException {
			LOGGER.info("Fin du parsing");
//			LOGGER.info("Resultats du parsing");
//			for(Event p : mDecodedEvent){
//				LOGGER.info(p);
//			}
		}
		
		private void updateEvent()
		{
			if ( mEvent.getType().equals("PropertyChangeEvent"))
			{
				mEvent = new PropertyChangeEvent(mEvent);
			} else if ( mEvent.getType().equals("DocumentEvent"))
			{
				mEvent = new DocumentEvent(mEvent);
			} 
		}
		
		public List<Event> getDecodedEvent() {
			return mDecodedEvent;
		}

		private static final Logger LOGGER = Logger.getLogger(XMLEventHandler.class);
		private static final String ROOT_ELEMENT = "events";
		private static final String EVENT_ELEMENT = "event"; 
		private static final String SRC_ELEMENT = "source"; 
		private static final String NAME_ELEMENT = "name";
		private static final String CLASS_ELEMENT = "class";
		private static final String TYPE_ELEMENT = "type";
		private static final String DATA_ELEMENT = "data";
		private static final String TIME_ELEMENT = "time";
		
		//FOR PROPERTYCHANGE EVENT
		private static final String PROPERTY_NAME = "propertyName";
		private static final String OLD_VALUE = "oldValue";
		private static final String NEW_VALUE = "newValue";
		//FOR DOCUMENT EVENT
		private static final String OFFSET = "offset";
		private static final String LENGTH = "lenght";
		private static final String CHANGE = "change";
	}
