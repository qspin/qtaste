package com.qspin.qtaste.tools.filter;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public final class FilterXmlHandler extends DefaultHandler {

	public FilterXmlHandler()
	{
		mDecodedFilters = new ArrayList<Filter>();
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ( qName.equals(Constant.XML_ROOT_TAG) )
		{
			mInFilterCollection = true;
		}
		else if (mInFilterCollection)
		{
			if( qName.equals(Constant.XML_FILTER_TAG) )
			{
				mCurrentFilter = new Filter();
			}
			else if ( mCurrentFilter!= null )
			{
				if( qName.equals(Constant.XML_ACCEPT_TAG) )
				{
					mBuffer = new StringBuilder();
				}
				else if( qName.equals(Constant.XML_REJECT_TAG) )
				{
					mBuffer = new StringBuilder();
				}
				else if( qName.equals(Constant.XML_SOURCE_TAG) )
				{
					mBuffer = new StringBuilder();
				}
				else if( qName.equals(Constant.XML_DESCRIPTION_TAG) )
				{
					mBuffer = new StringBuilder();
				}
			}
		}
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ( mInFilterCollection )
		{
			if ( qName.equals(Constant.XML_ROOT_TAG))
			{
				mInFilterCollection = false;
			}
			else if (mCurrentFilter != null )
			{
				if (qName.equals(Constant.XML_FILTER_TAG))
				{
					mDecodedFilters.add(mCurrentFilter);
					mCurrentFilter = null;
				}
				else {
					String value = mBuffer.toString();
					if (qName.equals(Constant.XML_REJECT_TAG))
					{
						mCurrentFilter.addRejectedEvent(getClassFor(value));
						mBuffer = null;
					}
					else if (qName.equals(Constant.XML_ACCEPT_TAG))
					{
						mCurrentFilter.addAcceptedEvent(getClassFor(value));
						mBuffer = null;
					}
					else if (qName.equals(Constant.XML_SOURCE_TAG))
					{
						mCurrentFilter.setSourceRule(getClassFor(value));
						mBuffer = null;
					}
					else if (qName.equals(Constant.XML_DESCRIPTION_TAG))
					{
						mCurrentFilter.setDescription(value);
						mBuffer = null;
					}
				}
			}
		}
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {
		String data = new String(ch, start, length);
		if (mBuffer != null)
			mBuffer.append(data);
	}

	public void startDocument() throws SAXException {
		mDecodedFilters.clear();
		mInFilterCollection = false;
		mBuffer = null;
	}

	public void endDocument() throws SAXException {
	}
	
	private Class<?> getClassFor(String pClassName)
	{
		try {
			return this.getClass().getClassLoader().loadClass(pClassName);
		} catch (ClassNotFoundException e) {
			LOGGER.warn("Cannot load/find the class " + pClassName);
		}
		return null;
	}
	
	public List<Filter> getDecodedFilters()
	{
		return new ArrayList<Filter>(mDecodedFilters);
	}
	
	private List<Filter> mDecodedFilters;
	private Filter mCurrentFilter;
	private boolean mInFilterCollection;
	private StringBuilder mBuffer;
	
	/** Used for logging. */
	private static final Logger LOGGER = Logger.getLogger(FilterXmlHandler.class);
}
