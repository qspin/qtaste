package com.qspin.qtaste.tools;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public abstract class AbstractGUIAnalyzer implements Runnable,
		ContainerListener {

	protected abstract boolean preProcess();

	protected abstract void processComponent(Component pComponent);

	protected abstract boolean postProcess();

	@Override
	public synchronized void componentAdded(ContainerEvent e) {
		if (e.getContainer() != null) {
			scanComponent(e.getContainer());
		}
	}

	@Override
	public synchronized void componentRemoved(ContainerEvent e) {
		if (e.getChild() instanceof Container) {
			((Container) e.getChild()).removeContainerListener(this);
		}
	}

	public void run() {
		int unecessaryLoop = 0;

		try {
			System.out.println("Thread start");
			if (preProcess()) {
				while (unecessaryLoop < 5) {
					process();
					if (!mProccessedWindow.isEmpty()) {
						unecessaryLoop = 0;
					} else {
						++unecessaryLoop;
					}
					Thread.sleep(500);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			postProcess();
			System.out.println("Thread End");
		}
	}

	protected AbstractGUIAnalyzer() {
		mProccessedWindow = new ArrayList<Window>();
	}

	protected void process() {
		for (int i = 0; i < Frame.getWindows().length; ++i) {
			Window windows = Frame.getWindows()[i];
			if (!mProccessedWindow.contains(windows)) {
				if (windows.isShowing()) {
					scanComponent(windows);
					mProccessedWindow.add(windows);
				}
			}
		}
	}

	protected void scanComponent(Component pComponent) {
		if (pComponent instanceof Window) {
			((Window) pComponent).addWindowListener(new WindowAdapter() {
				public void windowClosed(WindowEvent e) {
					mProccessedWindow.remove(e.getSource());
				}
			});
		}
		processComponent(pComponent);
		if (pComponent instanceof Container) {
			((Container) pComponent).addContainerListener(this);
			if (((Container) pComponent).getComponentCount() > 0) {
				for (int i = 0; i < ((Container) pComponent).getComponentCount(); ++i) {
					scanComponent(((Container) pComponent).getComponent(i));
				}
			}
		}
	}

	protected List<Window> mProccessedWindow;
	protected static final Logger LOGGER = Logger.getLogger(AbstractGUIAnalyzer.class);
}
