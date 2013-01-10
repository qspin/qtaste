package com.qspin.qtaste.recorder.tray;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.qspin.qtaste.recorder.Spy;

public class RecorderTray implements PropertyChangeListener {

	public RecorderTray(Spy pSpy)
	{
		//Check the SystemTray is supported
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        mSpy = pSpy;
        genUI();
        mSpy.addPropertyChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		System.out.println("property changed...");
		if (evt.getPropertyName().equals(Spy.ACTIVE_STATE_PROPERTY) )
		{
			System.out.println("Change to " + mSpy.isActive());
			if ( mSpy.isActive() )
			{
				mTray.getTrayIcons()[0].setImage(ACTIVE);
			}
			else
			{
				mTray.getTrayIcons()[0].setImage(UNACTIVE);
			}
		}
	}
	
	private void genUI()
	{
		final PopupMenu pop_up = new PopupMenu();
        final TrayIcon trayIcon =
                new TrayIcon(UNACTIVE);
       
        // Create a pop-up menu components
        MenuItem aboutItem = new MenuItem("About");
        CheckboxMenuItem cb1 = new CheckboxMenuItem("Spy active", mSpy.isActive());
        cb1.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				mSpy.setActive(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
        CheckboxMenuItem cb2 = new CheckboxMenuItem("Set tooltip");
        Menu displayMenu = new Menu("Display");
        MenuItem errorItem = new MenuItem("Error");
        MenuItem warningItem = new MenuItem("Warning");
        MenuItem infoItem = new MenuItem("Info");
        MenuItem noneItem = new MenuItem("None");
        MenuItem exitItem = new MenuItem("Exit");
       
        //Add components to pop-up menu
        pop_up.add(aboutItem);
        pop_up.addSeparator();
        pop_up.add(cb1);
        pop_up.add(cb2);
        pop_up.addSeparator();
        pop_up.add(displayMenu);
        displayMenu.add(errorItem);
        displayMenu.add(warningItem);
        displayMenu.add(infoItem);
        displayMenu.add(noneItem);
        pop_up.add(exitItem);
       
        trayIcon.setPopupMenu(pop_up);
        
        try {
            mTray.add(trayIcon);
        } catch (AWTException e) {
            System.err.println("TrayIcon could not be added.");
        }
	}
	
	private static Image createImage(String pImagePath)
	{
		System.out.println(RecorderTray.class.getResource(pImagePath));
		Image img = Toolkit.getDefaultToolkit().createImage(RecorderTray.class.getResource(pImagePath));
		System.out.println(img);
		return img;
	}
	
	private Spy mSpy;
	private final SystemTray mTray = SystemTray.getSystemTray();

	protected static final Image ACTIVE = createImage("/images/active.png");
	protected static final Image UNACTIVE = createImage("/images/unactive.jpg");
}
