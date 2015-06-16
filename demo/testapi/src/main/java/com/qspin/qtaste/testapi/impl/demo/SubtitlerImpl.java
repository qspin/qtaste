package com.qspin.qtaste.testapi.impl.demo;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.qspin.qtaste.testapi.api.Subtitler;
import com.qspin.qtaste.testsuite.QTasteException;

public final class SubtitlerImpl implements Subtitler {

	/**
	 * Constructor.
	 * @throws QTasteException
	 */
	public SubtitlerImpl() throws QTasteException {

		// initialization in the EDT
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					int width = Toolkit.getDefaultToolkit().getScreenSize().width;
					int height = 100;

					// create the subtitler window
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

					// create a timer to hide the subtitler window after a delay
					m_timer = new Timer(0, new ActionListener() {
						public void actionPerformed(ActionEvent event) {
							m_subtitleFrame.setVisible(false);
						}
					});
					m_timer.setRepeats(false);
				}
			});
		}
		catch (Exception e) {
            throw new QTasteException("Error while creating subtitler window", e);
		}
		
		initialize();
	}

	/**
	 * Initialize the execution of the subtitler.
	 * @throws QTasteException
	 */
	@Override
	public void initialize() throws QTasteException {
	}

	/**
	 * Stop the execution of the subtitler.
	 * @throws QTasteException
	 */
	@Override
	public void terminate() throws QTasteException {
		
		if (m_timer.isRunning()) {
			m_timer.stop();
	
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						m_subtitleFrame.setVisible(false);
					}
				});
			}
			catch (Exception e) {
	            throw new QTasteException("Error while hiding the subtitler", e);
			}
		}
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
	 * @param displayTimeMaxInSecond the maximum time while the subtitle is displayed.
	 * @throws QTasteException
	 */
	@Override
	public void setSubtitle(final String subtitle, final double displayTimeMaxInSecond) throws QTasteException {
		
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					m_subtitle.setText("<html><body>" + subtitle + "</body></html>");
					
					m_timer.setInitialDelay((int)(displayTimeMaxInSecond * 1000));
					m_timer.restart();
					
					m_subtitleFrame.toFront();
					m_subtitleFrame.setVisible(true);
				}
			});
		}
		catch (Exception e) {
            throw new QTasteException("Error while updating subtitler text", e);
		}
	}
	
	private JWindow m_subtitleFrame;
	private JLabel  m_subtitle;
	private Timer   m_timer;
}
