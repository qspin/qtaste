/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qspin.qtaste.sikuli.client;

import com.qspin.qtaste.kernel.testapi.SingletonComponent;
import com.qspin.qtaste.tcom.jmx.impl.JMXClient;
import com.qspin.qtaste.testsuite.QTasteException;

/**
 *
 */
public class Sikuli implements com.qspin.qtaste.sikuli.Sikuli, SingletonComponent {

	public Sikuli(String url) throws Exception {
		mClient = new JMXClient(url);
		if (mClient == null) {
			throw new QTasteException("Unable to connect to the JMX client (" + url + ")");
		}
		initialize();
	}
	
	public void initialize() throws QTasteException {
		try {
			mClient.connect();
			mProxy = getProxy();
		} catch (Exception pExc) {
			pExc.printStackTrace();
			throw new QTasteException("Unable to initialize the JMX client", pExc);
		}
	}

	@Override
	public void rightClick(String fileName) throws QTasteException {
		mProxy.rightClick(fileName);
	}

	@Override
	public void click(String fileName) throws QTasteException {
		mProxy.click(fileName);
	}

	@Override
	public void doubleClick(String fileName) throws QTasteException {
		mProxy.doubleClick(fileName);
	}

	@Override
	public void hover(String fileName) throws QTasteException {
		mProxy.hover(fileName);
	}

	@Override
	public void dragDrop(String targetFileName, String destinationFileName) throws QTasteException {
		mProxy.dragDrop(targetFileName, destinationFileName);
	}

	@Override
	public void takeSnapShot(String directory, String fileName) throws QTasteException {
		mProxy.takeSnapShot(directory, fileName);
	}

	@Override
	public void type(String fileName, String value) throws QTasteException {
		mProxy.type(fileName, value);
	}

	@Override
	public void type(String value) throws QTasteException {
		mProxy.type(value);
	}

	@Override
	public void paste(String fileName, String value) throws QTasteException {
		mProxy.paste(fileName, value);
	}

	@Override
	public void paste(String value) throws QTasteException {
		mProxy.paste(value);
	}

	@Override
	public boolean exist(String fileName) throws QTasteException {
		return mProxy.exist(fileName);
	}

	@Override
	public void wait(String fileName) throws QTasteException {
		mProxy.wait(fileName);
	}

	@Override
	public void wait(String fileName, double timeout) throws QTasteException {
		mProxy.wait(fileName, timeout);
	}

	@Override
	public void waitVanish(String fileName) throws QTasteException {
		mProxy.waitVanish(fileName);
	}

	@Override
	public void waitVanish(String fileName, double timeout) throws QTasteException {
		mProxy.waitVanish(fileName, timeout);
	}
	
	@Override
	public void openAndRunScript(String scriptPath) throws QTasteException
	{
		mProxy.openAndRunScript(scriptPath);
	}

	private com.qspin.qtaste.sikuli.Sikuli getProxy() throws Exception {
		return (com.qspin.qtaste.sikuli.Sikuli) mClient.getProxy(BEAN_NAME, BEAN_INTERFACE);
	}

	public void terminate() throws QTasteException {
		try {
			mClient.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			throw new QTasteException(e.getMessage());
		}
	}

	protected com.qspin.qtaste.sikuli.Sikuli mProxy;
	protected JMXClient mClient;
	private static final String BEAN_NAME = "com.qspin.qtaste.sikuli.server:type=Sikuli";
	private static final Class<?> BEAN_INTERFACE = com.qspin.qtaste.sikuli.Sikuli.class;

}
