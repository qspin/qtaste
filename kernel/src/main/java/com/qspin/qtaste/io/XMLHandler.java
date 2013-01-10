package com.qspin.qtaste.io;

import java.util.LinkedList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.qspin.qtaste.testsuite.TestRequirement;

public class XMLHandler extends DefaultHandler {
	private List<TestRequirement> mDecodedRequirement;
	private TestRequirement mRequirement;
	private StringBuffer mBuffer;
	
	private String mCurrentRequirementElement;

	public XMLHandler() {
		super();
		mDecodedRequirement = new LinkedList<TestRequirement>();
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equals(XMLFile.ROOT_ELEMENT)) {
			mDecodedRequirement = new LinkedList<TestRequirement>();
		} else if (qName.equals(XMLFile.REQUIREMENT_ELEMENT)) {
			mRequirement = new TestRequirement(attributes.getValue(XMLFile.REQUIREMENT_ID));
		} else if ( mCurrentRequirementElement == null )
		{
			mBuffer = new StringBuffer();
			mCurrentRequirementElement = qName;
		} else if ( mBuffer!= null ) {
			mBuffer.append("<");
			mBuffer.append( qName.replace(XMLFile.SPACE_REPLACEMENT, " ") );
			for ( int i = 0; i<attributes.getLength(); i++ )
			{
				mBuffer.append(" " + attributes.getQName(i).replace(XMLFile.SPACE_REPLACEMENT, " ") + "=\"" + attributes.getValue(i) + "\"");
			}
			mBuffer.append(">");
			System.out.println("buffer : " + mBuffer);
		}
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (qName.equals(XMLFile.REQUIREMENT_ELEMENT)) {
			mDecodedRequirement.add(mRequirement);
			mRequirement = null;
		} else if (mCurrentRequirementElement!= null && qName.equals(mCurrentRequirementElement)) {
			if ( qName.equals(XMLFile.DESCRIPTION_ELEMENT) )
			{
				mRequirement.setRequirementDescription(mBuffer.toString());
			} else {
				mRequirement.setData(qName.replace(XMLFile.SPACE_REPLACEMENT, " "), mBuffer.toString());
			}
			mBuffer = null;
			mCurrentRequirementElement = null;
		} else if ( mCurrentRequirementElement != null && mBuffer != null ) {
			mBuffer.append("</" + qName.replace(XMLFile.SPACE_REPLACEMENT, " ") + ">");
		}
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		String readData = new String(ch, start, length);
		if (mBuffer != null)
			mBuffer.append(readData);
	}

	public void startDocument() throws SAXException {
		mDecodedRequirement.clear();
	}

	public void endDocument() throws SAXException {}

	public List<TestRequirement> getDecodedRequirement() {
		return mDecodedRequirement;
	}
}
