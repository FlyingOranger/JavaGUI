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
    private MenuItem notificationPlaceHolder;
    private CheckboxMenuItem startupItem;

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

        startupItem = new CheckboxMenuItem("Run on startup");
        startupItem.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                int id = e.getStateChange();
                if (id == ItemEvent.SELECTED)
                    Connector.getInstance().sendConfig(Connector.runOnStartup, "true");
                else
                    Connector.getInstance().sendConfig(Connector.runOnStartup, "false");
            }
        });

        MenuItem aboutItem = new MenuItem("About");
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Connector.getInstance().openLink("https://github.com/RedditCanFly/RedditCanFly");
            }
        });

        MenuItem exit = new MenuItem("Exit");
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Connector.getInstance().sendExit();
                System.exit(0);
            }
        });

        notificationPlaceHolder = new MenuItem("Future Notifications");
        notificationPlaceHolder.setEnabled(false);

        popMen.add( notificationPlaceHolder);
        popMen.addSeparator();
        popMen.add(disable);
        popMen.add(startupItem);
        popMen.addSeparator();
        popMen.add(aboutItem);
        popMen.add(exit);

        return popMen;
    }

    public void addNotification(String title, String link){

        if (isSupported) {
            final PopupMenu pMenu = trayIcon.getPopupMenu();

            // we do not want to add this item if the link is already present in the list
            // to keep track of the item's link, we make a new class LinkMenuItem
            for (int i = 0; i < pMenu.getItemCount(); i++) {

                MenuItem item = pMenu.getItem(i);
                if (item instanceof LinkMenuItem){
                    if ( ((LinkMenuItem) item).getLink().equals(link) )
                        return;
                }
            }

            // the new item to add
            LinkMenuItem newItem = new LinkMenuItem(title, link);
            newItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    pMenu.remove(newItem);
                    Connector.getInstance().openLink(newItem.getLink());
                    if (!(pMenu.getItem(0) instanceof LinkMenuItem)){
                        pMenu.insert(notificationPlaceHolder, 0);
                        trayIcon.setImage(grayEnvelope);
                        trayIcon.setToolTip("no mail :(");
                    }

                }
            });

            // if the first item is not a link item... then it's the gray placeholder
            if (!(pMenu.getItem(0) instanceof LinkMenuItem))
                pMenu.remove(0);

            // add the new item
            pMenu.insert(newItem, 0);

            trayIcon.setToolTip("You've got mail!");
            trayIcon.setImage(orangeEnvelope);
        }

    }

    public void setStartupState(String value){

        boolean val = Boolean.parseBoolean(value);
        startupItem.setState(val);

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
