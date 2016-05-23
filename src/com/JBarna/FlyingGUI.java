package com.JBarna;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by JBarna on 4/23/2016.
 */
public class FlyingGUI extends JWindow implements ActionListener {

    private static FlyingGUI INSTANCE;

    private ImageIcon envelope;
    private int DELAY = 10;
    private double screenWidth;
    private Timer animationTimer;
    private JLabel titleLabel;
    private boolean weAreFlying;

    public static FlyingGUI getInstance(){
        if (INSTANCE == null)
            INSTANCE = new FlyingGUI();

        return INSTANCE;
    }

    private FlyingGUI(){

        setUpGUI();
        weAreFlying = true;
        animationTimer = new Timer(DELAY, this);

    }

    private void getDimensions(){

        screenWidth = 0;

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        for(GraphicsDevice curGs : gs)
        {
            DisplayMode dm = curGs.getDisplayMode();
            screenWidth += dm.getWidth();
        }


    }

    private void setUpGUI(){

        this.setAlwaysOnTop(true);
        this.setBackground(new Color(0, 0, 0, 0.5f));

        envelope = new ImageIcon(getClass().getClassLoader().getResource("orange_envelope.png"));
        envelope.setImage(envelope.getImage().getScaledInstance(-1, 50, Image.SCALE_DEFAULT));

        titleLabel = new JLabel("HELLO");
        titleLabel.setForeground(new Color(255, 150, 0));
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.PLAIN, 30));
        JPanel jlabel = new JPanel();
        jlabel.add(titleLabel);
        jlabel.setBackground(new Color(0, 100, 100));

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));

        this.add(Box.createRigidArea( new Dimension(15, 0)));
        this.add(titleLabel);
        this.add(Box.createRigidArea( new Dimension(15, 0)));
        this.add(new JLabel(envelope));

        this.pack();

    }

    public void startFlying(String title){

            getDimensions();
            this.setLocation(0 - this.getWidth(), 0);
            titleLabel.setText(title);
            this.pack();
            this.setVisible(true);
            animationTimer.start();

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        int xLoc = this.getLocation().x;

        if (xLoc < screenWidth) {
            this.setLocation(xLoc + 5, 0);

        }
        else {
            stopFlying();
            Connector.getInstance().waitForNewMessage();
        }

    }

    public void stopFlying(){
        animationTimer.stop();
        this.setVisible(false);
    }
}
