package com.JBarna;

import javax.swing.*;
import java.awt.*;

public class Main {

    FlyingGUI gui;
    Connector connector;
    TrayIconManager iconManager;

    public Main() {

        TrayIconManager.getInstance();

        Connector.getInstance().waitForNewMessage();

    }

    public static void main(String[] args) {

        new Main();
    }
}
