package simulation.ConnectionSimulations;

import connection.MessageListener;
import connection.Subscriber;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

public class RunnerSubscriberSimulator implements Subscriber<String>, ActionListener {
    MessageListener messageListener;
    JFrame frame;
    JTextArea field;

    public RunnerSubscriberSimulator(){
        frame = new JFrame("Custom properties");
        frame.setSize(550, 500);
        frame.setLocationRelativeTo(null);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        field = new JTextArea();
        field.setBorder(BorderFactory.createCompoundBorder(
                field.getBorder(),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        JScrollPane sp = new JScrollPane(field);
        panel.add(sp, BorderLayout.CENTER);

        JButton send = new JButton("Send");
        send.addActionListener(this);
        panel.add(send, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);
    }

    @Override
    public void subscribe(String topic) {

    }

    @Override
    public void receive(String topic, String message) {

    }

    @Override
    public void addListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    @Override
    public void disconnect() {
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String message = field.getText();
        messageListener.messageArrived("", message);
    }
}
