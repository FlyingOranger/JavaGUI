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
    private Menu appsMenu;
    private CheckboxMenuItem startupItem;
    private CheckboxMenuItem disableAllFlying;

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


        disableAllFlying = new CheckboxMenuItem("Disable flying notifications");
        disableAllFlying.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                int id = e.getStateChange();
                if (id == ItemEvent.SELECTED)
                    FlyingGUI.getInstance().stopFlying();

                Connector.getInstance().sendConfig( Connector.disableAllFlying, id == ItemEvent.SELECTED ? "true": "false");
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

        appsMenu = new Menu("Apps");
        MenuItem editApps = new MenuItem("Edit apps");
        editApps.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Connector.getInstance().openLink(Connector.editApps);
            }
        });
        appsMenu.add( editApps );


        MenuItem aboutItem = new MenuItem("About");
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Connector.getInstance().openLink("https://github.com/FlyingOranger/FlyingOranger");
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
        popMen.add(disableAllFlying);
        popMen.add(startupItem);
        popMen.add(appsMenu);
        popMen.addSeparator();
        popMen.add(aboutItem);
        popMen.add(exit);

        return popMen;
    }

    public void addApp(String appName) {

        Menu newApp = new Menu(appName);

        appsMenu.insert(newApp, 0);

    }

    public void createAppConfig( final String appName, final String configName, boolean value){

        // get the specific app menu
        for (int i = 0; i < appsMenu.getItemCount(); i++ ){

            if ( appsMenu.getItem(i).getLabel().equals(appName)){
                Menu app = (Menu) appsMenu.getItem(i);

                CheckboxMenuItem cbox = new CheckboxMenuItem(configName);
                cbox.setState( value );
                cbox.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        int id = e.getStateChange();
                        Connector.getInstance().sendConfig(Connector.appConfig, appName, configName,
                                id == ItemEvent.SELECTED ? "true": "false");
                    }
                });

                app.add( cbox );
                break;
            }

        }

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

    public void setStartupState(boolean value){

        startupItem.setState(value);

    }

    public void setDisableAllFlyingState( boolean value ){

        disableAllFlying.setState( value );

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
