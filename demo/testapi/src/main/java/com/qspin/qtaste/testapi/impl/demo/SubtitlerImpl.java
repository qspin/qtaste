package com.qspin.qtaste.testapi.impl.demo;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

import com.qspin.qtaste.testapi.api.Subtitler;
import com.qspin.qtaste.testsuite.QTasteException;

public final class SubtitlerImpl implements Subtitler, Runnable {

	public SubtitlerImpl() throws QTasteException
	{
		super();
		
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = 100;
		
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
		
		initialize();
	}

	@Override
	public void initialize() throws QTasteException
	{
		m_thread = new Thread(this);
		m_thread.start();
	}

	@Override
	public void terminate() throws QTasteException {
		m_subtitleFrame.setVisible(false);
		m_abort = true;
	}

	@Override
	public void setSubtitle(String subtitle) {
		setSubtitle(subtitle, 3);
	}
	
	@Override
	public void setSubtitle(String subtitle, double displayTimeInSecond) {
		m_subtitle.setText("<html><body>" + subtitle + "</body></html>");
		setDisplayTime(displayTimeInSecond);
		m_subtitleFrame.toFront();
		m_subtitleFrame.setVisible(true);
		System.out.println("change the frame visibility (true)");
		
	}

	public void run() {
		while ( !m_abort )
		{
			if ( m_subtitleFrame.isVisible() )
			{
				if (System.currentTimeMillis() > (m_startTime + m_displayTimeInSec*1000) )
				{
					m_subtitleFrame.setVisible(false);
				}
			}
			try{
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		m_subtitleFrame.setVisible(false);
	}

		
	public void setDisplayTime(double displayTimeInSecond)
	{
		m_displayTimeInSec = displayTimeInSecond;
		m_startTime = System.currentTimeMillis();
		m_abort = false;
	}
	
	private final JWindow m_subtitleFrame = new JWindow();
	private JLabel m_subtitle;
	private double m_displayTimeInSec;
	private long m_startTime;
	private Thread m_thread;
	private boolean m_abort = false;
}
