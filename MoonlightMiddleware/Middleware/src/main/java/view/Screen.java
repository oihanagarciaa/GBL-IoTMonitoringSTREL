package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class Screen extends JFrame {
    //ActionListener actionListener;
    JButton jbsubscriber, jbSend;
    JTextField sensorsMessages, sendingMoonlight;

    public Screen(){
        this.setSize(600, 400);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        //this.getContentPane().setBackground( new Color(235, 222, 240));
        this.setContentPane(panelCentral());
        this.setVisible(true);
    }

    public Container panelCentral() {
        JSplitPane panel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                panelSensors(), panelMoonlight());
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        panel.setOneTouchExpandable(false);
        panel.setDividerLocation(300);

        return panel;
    }

    public Component panelSensors() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("iot/sensors");
        jbsubscriber = new JButton("Subscribe");

        sensorsMessages = new JTextField("Subscribe to the topic");;
        sensorsMessages.setEditable(false);


        panel.add(title, BorderLayout.NORTH);
        panel.add(sensorsMessages, BorderLayout.CENTER);
        panel.add(jbsubscriber, BorderLayout.SOUTH);

        return panel;
    }

    private Component panelMoonlight() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("iot/moonlight");

        sendingMoonlight = new JTextField("Message...");
        jbSend = new JButton("Send message");

        panel.add(title, BorderLayout.NORTH);
        panel.add(sendingMoonlight, BorderLayout.CENTER);
        panel.add(jbSend, BorderLayout.SOUTH);

        return panel;
    }

    public void addJButtonListener(ActionListener listener) {
        jbsubscriber.addActionListener(listener);
        jbSend.addActionListener(listener);
    }

    public void setRecievedMessage(String message){
        sensorsMessages.setText(message);
    }

    public String getSendingMessage(){
        return sendingMoonlight.getText();
    }

}
