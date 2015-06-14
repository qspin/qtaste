package com.qspin.qtaste.testapi.impl.demo;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.qspin.qtaste.testapi.api.Subtitler;
import com.qspin.qtaste.testsuite.QTasteException;

public final class SubtitlerImpl implements Subtitler, Runnable {

	/**
	 * Constructor.
	 * @throws QTasteException
	 */
	public SubtitlerImpl() throws QTasteException {
		super();		

		// window initialization in the EDT
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					int width = Toolkit.getDefaultToolkit().getScreenSize().width;
					int height = 100;
					
					m_subtitleFrame = new JWindow();
					
					m_subtitleFrame.setPreferredSize(new Dimension(width, height));
					m_subtitleFrame.setLocation(0, Toolkit.getDefaultToolkit().getScreenSize().height - height);
					m_subtitleFrame.setBackground(null);
					
					m_subtitleFrame.setLayout(new GridLayout());
					m_subtitle = new JLabel();
					Font f = new Font(Font.SERIF, Font.PLAIN, 20);
					m_subtitle.setFont(f);
					m_subtitle.setHorizontalAlignment(SwingConstants.CENTER);
					m_subtitleFrame.add(m_subtitle);
	
					m_subtitleFrame.pack();
				}
			});
		}
		catch (Exception e) {
            throw new QTasteException("Error while creating subtitler window", e);
		}
	}

	/**
	 * Initialize the execution of the subtitler.
	 * @throws QTasteException
	 */
	@Override
	public void initialize() throws QTasteException {
		m_abort = false;

		m_thread = new Thread(this);
		m_thread.start();		
	}

	/**
	 * Stop the execution of the subtitler.
	 * @throws QTasteException
	 */
	@Override
	public void terminate() throws QTasteException {
		m_abort = true;
	}

	/**
	 * Set the current subtitle.
	 * @param subtitle the new subtitle
	 * @throws QTasteException
	 */
	@Override
	public void setSubtitle(String subtitle) throws QTasteException {
		setSubtitle(subtitle, 3);
	}
	
	/**
	 * Set the current subtitle and the display time.
	 * @param subtitle the new subtitle
	 * @param displayTimeInSecond the time while the subtitle is displayed.
	 * @throws QTasteException
	 */
	@Override
	public void setSubtitle(final String subtitle, final double displayTimeInSecond) throws QTasteException {
		
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					m_subtitle.setText("<html><body>" + subtitle + "</body></html>");
		
					System.out.println("subtitle:" + m_subtitle.getText());

					m_displayTimeInSec = displayTimeInSecond;
					m_startTime = System.currentTimeMillis();
					m_subtitleFrame.toFront();
					m_subtitleFrame.setVisible(true);
				}
			});
		}
		catch (Exception e) {
            throw new QTasteException("Error while updating subtitler text", e);
		}
	}

	/**
	 * while the thread is needed, this method checks every second 
	 * if the subtitle frame has to be hidden or not. 
	 */
	public void run() {
		
		while ( !m_abort ) {
			
			if ( m_subtitleFrame.isVisible() ) {
				if (System.currentTimeMillis() > (m_startTime + m_displayTimeInSec * 1000) ) {
					setVisible(false);
				}
			}
			
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		setVisible(false);
	}

	/**
	 * Change the subtitler window visibility in the EDT.
	 * @param the new visibility state.
	 */
	private void setVisible(final boolean state) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					m_subtitleFrame.setVisible(state);
				}
			});
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private JWindow m_subtitleFrame;
	private JLabel  m_subtitle;
	private double  m_displayTimeInSec;
	private long 	m_startTime;
	private Thread  m_thread;
	private boolean m_abort = false;
}
