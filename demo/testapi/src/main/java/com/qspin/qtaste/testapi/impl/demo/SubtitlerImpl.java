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

public final class SubtitlerImpl implements Subtitler {

	public SubtitlerImpl() throws QTasteException
	{
//		// Determine what the default GraphicsDevice can support.
//		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//		GraphicsDevice gd = ge.getDefaultScreenDevice();
//
//		boolean isUniformTranslucencySupported = gd.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.TRANSLUCENT);
//		boolean isPerPixelTranslucencySupported = gd.isWindowTranslucencySupported(PERPIXEL_TRANSLUCENT);
//		boolean isShapedWindowSupported = gd.isWindowTranslucencySupported(PERPIXEL_TRANSPARENT);
		
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = 100;
		
		m_subtitleFrame.setPreferredSize(new Dimension(width, height));
		m_subtitleFrame.setLocation(0, Toolkit.getDefaultToolkit().getScreenSize().height - height);
//		Color backgroundColor = new Color(50, 0, 0, 230);
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
	}

	@Override
	public void terminate() throws QTasteException {
		m_subtitleFrame.setVisible(false);
		if ( m_t != null && m_visibilityThread.isRunning() )
		{
			System.out.println("kill the thread");
			m_t.stop();
		}
	}

	@Override
	public void setSubtitle(String subtitle) {
		setSubtitle(subtitle, 3);
	}
	@Override
	public void setSubtitle(String subtitle, double displayTimeInSecond) {
		System.out.println("change text to '" + subtitle + "'");
		m_subtitle.setText("<html><body>" + subtitle + "</body></html>");
		if ( m_t != null && m_visibilityThread.isRunning() )
		{
			System.out.println("kill the thread");
			m_t.stop();
		}
		m_visibilityThread.setDisplayTime(displayTimeInSecond);
		m_t = new Thread(m_visibilityThread);
		m_t.start();
	}

	private final JWindow m_subtitleFrame = new JWindow();
	private JLabel m_subtitle;
	private Thread m_t;
	private final VisibilityThread m_visibilityThread = new VisibilityThread();
	
	private class VisibilityThread implements Runnable
	{
		VisibilityThread()
		{
			m_run = false;
		}

		@Override
		public void run() {
			m_run = true;
			System.out.println("change the frame visibility (true)");
			m_subtitleFrame.toFront();
			m_subtitleFrame.setVisible(true);
			try {
				System.out.println("wait 3 sec");
				Thread.sleep((int) (m_displayTimeInSec * 1000));
				System.out.println("change the frame visibility (false)");
				m_subtitleFrame.setVisible(false);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				m_run = false;
			}
		}
		
		public boolean isRunning()
		{
			return m_run;
		}
		
		public void setDisplayTime(double displayTimeInSecond)
		{
			m_displayTimeInSec = displayTimeInSecond;
		}
		
		private boolean m_run;
		private double m_displayTimeInSec;
	}
}
