import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;



public class Server extends JFrame {

    private JTextField         textBox;
    private JTextArea          displayArea;
    private JScrollPane        scrollPane;
    private ObjectOutputStream output;
    private ObjectInputStream  input;
    private Socket             connection;
    private ServerSocket       server;
    private String             msgFromClient;

    private final static String endl = "\n";

    public Server() {
        super("ServerChatBox");
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        /*textbox*/
        textBox = new JTextField();
        //textBox.setEditable(false);
        textBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendData(e.getActionCommand());
                textBox.setText( "" );
            }
        });

        /* displayArea */
        displayArea = new JTextArea();
        displayArea.setEditable(false);

        /* adding in Jframe */
        scrollPane = new JScrollPane(displayArea);
        scrollPane.setVerticalScrollBarPolicy(22);
        add(textBox, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        setSize(250,300);
        setVisible(true);
    }

    public void runServer() {
        try {
            server = new ServerSocket(1111);
            waitForConnection();
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


    private void waitForConnection()  {
        displayMessage("Server is waiting.....");
        try {
            connection = server.accept();
        } catch (IOException e) {}

        displayMessage("Server is connected with client....");
    }

    private void getStream() {
        try {
            output = new ObjectOutputStream(connection.getOutputStream());
            input = new ObjectInputStream(connection.getInputStream());
        } catch (IOException e) {}
    }

    private void processConnection() {

        //textBox.setEditable(true);
        while(true) {

            try {
                msgFromClient = (String) input.readObject();
                displayMessage(msgFromClient);
            }
            catch (IOException e) {}
            catch (ClassNotFoundException e) {}
        }
    }


    private void sendData(String msg) {

        try {
            output.writeObject(msg);
            output.flush();
            displayMessage(msg);
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