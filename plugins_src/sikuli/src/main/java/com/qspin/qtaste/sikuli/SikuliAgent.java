package com.qspin.qtaste.sikuli;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import com.qspin.qtaste.sikuli.server.Sikuli;

public class SikuliAgent {
    private final static Image ICON_TRAY;

    static {
        try {
            ICON_TRAY = ImageIO.read(SikuliAgent.class.getResourceAsStream("/tray.png"));
        } catch (Exception ex) {
            throw new RuntimeException("Cannot load Sikuli tray icon : " + ex.getMessage(), ex);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                final Sikuli server = new Sikuli();
                TrayIcon sikuliIcon = new TrayIcon(ICON_TRAY);
                MenuItem quit = new MenuItem("Stop Sikuli Qtaste Agent");
                quit.addActionListener(e -> {
                    try {
                        server.unregister();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    System.exit(0);
                });
                sikuliIcon.setPopupMenu(new PopupMenu());
                sikuliIcon.getPopupMenu().add(quit);
                SystemTray.getSystemTray().add(sikuliIcon);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

}
