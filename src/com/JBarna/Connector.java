package com.JBarna;

import javax.swing.*;
import java.util.Scanner;

/**
 * Created by JBarna on 4/23/2016.
 */
public class Connector {
    public final static String runOnStartup = "run_on_startup";
    public final static String createApp = "create_app";
    public final static String appConfig = "app_config";
    public final static String disableAllFlying = "disable_all_flying_notifications";
    public final static String editApps = "edit_apps";



    private static Connector INSTANCE;
    private final static String DELIM = ",";
    private Scanner in;


    public static Connector getInstance(){

        if (INSTANCE == null)
            INSTANCE = new Connector();

        return INSTANCE;

    }

    private Connector(){
        in = new Scanner(System.in);
        in.useDelimiter(",");
    }

    public void waitForNewMessage() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                // just beware, if you're testing, when you press enter you send a '\n'
                String first = in.next();

                if (first.equals("EXIT"))
                    System.exit(0);

                else if (first.equals("CONFIG")){

                    String configKey = in.next();
                    String configValue = in.next();

                    if (configKey.equals(runOnStartup))
                        TrayIconManager.getInstance().setStartupState( Boolean.parseBoolean(configValue));

                    else if (configKey.equals(createApp))
                        TrayIconManager.getInstance().addApp( configValue );

                    else if (configKey.equals( disableAllFlying )){

                        TrayIconManager.getInstance().setDisableAllFlyingState( Boolean.parseBoolean(configValue) );

                    }

                    else if (configKey.equals(appConfig)){

                        String appName = configValue;
                        String configName = in.next();
                        boolean value = Boolean.parseBoolean( in.next() );

                        TrayIconManager.getInstance().createAppConfig(appName, configName, value);

                    }

                    waitForNewMessage();

                } else if (first.equals("LINK")) {

                    final String title = in.next();
                    String link = in.next();
                    boolean fly = Boolean.parseBoolean( in.next() );

                    TrayIconManager.getInstance().addNotification(title, link);

                    if (fly){
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                FlyingGUI.getInstance().startFlying(title);
                            }
                        });
                    }
                    else waitForNewMessage();
                } else
                    waitForNewMessage();
            }
        }).start();
    }

    public void openLink(String link){
        System.out.print("LINK" + DELIM + link + DELIM);
    }

    public void sendConfig(String ...config){
        StringBuilder configBuilder = new StringBuilder();

        for (String _config: config){
            configBuilder.append(_config).append(DELIM);
        }

        System.out.print("CONFIG" + DELIM + configBuilder.toString());
    }

    public void sendExit(){
        System.out.print("EXIT" + DELIM);
    }
}
