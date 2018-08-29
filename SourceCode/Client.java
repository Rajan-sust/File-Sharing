import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Client extends JFrame {

    private JTextField         textBox;
    private JTextArea          displayArea;
    private JScrollPane        scrollPane;
    private ObjectOutputStream output;
    private ObjectInputStream  input;
    private Socket             connection;
    private String             msgFromServer;

    private final static String endl = "\n";
    private final static String space = "          ";

    public Client() {
        super("ClientChatBox");
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        /*textbox*/
        textBox = new JTextField();
        new TextPrompt("Type a message...",textBox);
        setEdit(false);
        textBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendData(e.getActionCommand());
                textBox.setText( "" );
            }
        });

        /* displayArea */
        displayArea = new JTextArea();
        displayArea.setForeground(Color.BLUE);
        displayArea.setEditable(false);

        /* adding in Jframe */
        scrollPane = new JScrollPane(displayArea);
        scrollPane.setVerticalScrollBarPolicy(22);
        add(textBox, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        setSize(250,300);
        setVisible(true);
    }

    public void runClient() {
        try {
            connection = new Socket("127.0.0.1",1111);
            getStream();
            processConnection();
        }
        catch (IOException e) {}

        finally {

            try {
                input.close();
                output.close();
                connection.close();
            } catch (IOException e) {}
        }
    }




    private void getStream() {
        try {
            output = new ObjectOutputStream(connection.getOutputStream());
            input = new ObjectInputStream(connection.getInputStream());
        } catch (IOException e) {}
    }

    private void processConnection() {

        setEdit(true);
        while(true) {

            try {
                msgFromServer = (String) input.readObject();
                displayMessage(msgFromServer);
            }
            catch (IOException e) {}
            catch (ClassNotFoundException e) {}
        }
    }


    private void setEdit(boolean b) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                textBox.setEditable(b);
            }
        });
    }


    private void sendData(String msg) {

        try {
            output.writeObject(msg);
            output.flush();
            displayMessage(space+space+space+space+msg);
        } catch (IOException e) {}
    }

    private void displayMessage(String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                displayArea.append(endl + msg);
            }
        });
    }
}