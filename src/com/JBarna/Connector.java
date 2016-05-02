package com.JBarna;

import javax.swing.*;
import java.awt.color.ICC_ColorSpace;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by JBarna on 4/23/2016.
 */
public class Connector {
    public final static String runOnStartup = "run_on_startup";

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
                String title = in.next();

                if (title.equals("EXIT"))
                    System.exit(0);

                else if (title.equals("CONFIG")){

                    String configKey = in.next();
                    String configValue = in.next();

                    if (configKey.equals("run_on_startup"))
                        TrayIconManager.getInstance().setStartupState(configValue);

                    waitForNewMessage();

                } else {

                    String link = in.next();

                    if (!link.equals("empty_link"))
                        TrayIconManager.getInstance().addNotification(title, link);

                    if (!title.equals("empty_title")) {
                        final String _title = title;
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                FlyingGUI.getInstance().go(_title);
                            }
                        });
                    }
                }
            }
        }).start();
    }

    public void openLink(String link){
        System.out.print("LINK" + DELIM + link + DELIM);
    }

    public void sendConfig(String key, String value){
        System.out.print("CONFIG" + DELIM + key + DELIM + value + DELIM);
    }

    public void sendExit(){
        System.out.print("EXIT" + DELIM);
    }
}
