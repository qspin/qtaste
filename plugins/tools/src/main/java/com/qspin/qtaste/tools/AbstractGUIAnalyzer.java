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

/**
 * Abstract class to process GUI component.
 * @author simjan
 *
 */
public abstract class AbstractGUIAnalyzer implements Runnable,
		ContainerListener {

	/**
	 * The process to be done before starting the analyze.
	 * @return <code>true</code> is success.
	 */
	protected abstract boolean preProcess();

	/**
	 * The process to be done on each component.
	 * @param pComponent
	 */
	protected abstract void processComponent(Component pComponent);

	/**
	 * The process to be done at the end of the thread.
	 * @return <code>true</code> if success.
	 */
	protected abstract boolean postProcess();

	@Override
	/**
	 * Calls the #scanComponent(ContainerEvent) on the event's container.
	 */
	public synchronized void componentAdded(ContainerEvent e) {
		if (e.getContainer() != null) {
			scanComponent(e.getContainer());
		}
	}

	@Override
	/**
	 * If the removed component is a container, stops to listen it.
	 */
	public synchronized void componentRemoved(ContainerEvent e) {
		if (e.getChild() instanceof Container) {
			((Container) e.getChild()).removeContainerListener(this);
		}
	}

	public void run() {
		int unecessaryLoop = 0;

		try {
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
		}
	}

	/**
	 * Constructor.
	 */
	protected AbstractGUIAnalyzer() {
		mProccessedWindow = new ArrayList<Window>();
	}

	/**
	 * Calls {@link #scanComponent(Component)} once for all windows ({@link Frame#getWindows()})
	 */
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

	/**
	 * Analyzes the component.<br/>
	 * Registers itself to the component if it's a {@link Container} in order to be alerted if a component is added/removed.<br/>
	 * Registers itself to the component if it's a {@link Window} in order to be alerted if the window is closed.
	 * @param pComponent
	 */
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

	/** List of the analyzed and opened windows. */
	protected List<Window> mProccessedWindow;
	/** Used for logging. */
	protected static final Logger LOGGER = Logger.getLogger(AbstractGUIAnalyzer.class);
}
