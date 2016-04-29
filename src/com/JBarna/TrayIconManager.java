package com.JBarna;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by JBarna on 4/25/2016.
 */
public class TrayIconManager {

    private static TrayIconManager INSTANCE;

    private SystemTray systemTray;
    private Image orangeEnvelope, grayEnvelope;
    private boolean isSupported;
    private TrayIcon trayIcon;

    public static TrayIconManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new TrayIconManager();
        }

        return INSTANCE;
    }

    private TrayIconManager() {

        isSupported = SystemTray.isSupported();
        if (isSupported) {

            systemTray = SystemTray.getSystemTray();
            orangeEnvelope = new ImageIcon(getClass().getClassLoader().getResource("orange_envelope.png")).getImage();

            grayEnvelope = new ImageIcon(getClass().getClassLoader().getResource("gray_envelope.png")).getImage();

            trayIcon = new TrayIcon(grayEnvelope, "no mail :(");
            trayIcon.setImageAutoSize(true);

            trayIcon.setPopupMenu( createRightPopupMenu() );

            try {
                systemTray.add(trayIcon);
            } catch (AWTException e) {
                isSupported = false;
            }
        }
    }

    public void addNotification(String title, String link){

        if (isSupported) {

            for (int i = 0; i < trayIcon.getPopupMenu().getItemCount(); i++) {

                MenuItem item = trayIcon.getPopupMenu().getItem(i);
                if (item instanceof LinkMenuItem){
                    if ( ((LinkMenuItem) item).getLink().equals(link) )
                        return;
                }
            }

            MenuItem newItem = new LinkMenuItem(title, link);
            newItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    trayIcon.getPopupMenu().remove(newItem);
                    System.out.print(link + ",");
                    if (trayIcon.getPopupMenu().getItem(2).getLabel().equals("-")) {
                        trayIcon.setImage(grayEnvelope);
                        trayIcon.setToolTip("no mail :(");
                    }

                }
            });

            trayIcon.getPopupMenu().insert(newItem, 2);
            trayIcon.setToolTip("You've got mail!");
            trayIcon.setImage(orangeEnvelope);
        }

    }

    private PopupMenu createRightPopupMenu(){

        PopupMenu popMen = new PopupMenu();


        CheckboxMenuItem disable = new CheckboxMenuItem("Disable flying notifications");
        disable.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                int id = e.getStateChange();
                if (id == ItemEvent.SELECTED)
                    FlyingGUI.getInstance().setFlyingState(false);
                else
                    FlyingGUI.getInstance().setFlyingState(true);
            }
        });

        MenuItem aboutItem = new MenuItem("About");
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.print("www.google.com,");
            }
        });

        MenuItem exit = new MenuItem("Exit");
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.print("EXIT,");
                System.out.flush();
                System.exit(0);
            }
        });

        popMen.add("Notifications");
        popMen.addSeparator();
        popMen.addSeparator();
        popMen.add(disable);
        popMen.addSeparator();
        popMen.add(aboutItem);
        popMen.add(exit);

        return popMen;
    }

    private static class LinkMenuItem extends MenuItem {

        private String link;

        public LinkMenuItem(String title, String link){
            super(title);
            this.link = link;
        }

        public String getLink(){
            return link;
        }

    }
}
