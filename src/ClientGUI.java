import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;

public class ClientGUI {
    private static final String SERVER_ADDRESS = "0.0.0.0"; //REPLACE WITH LOCAL IP ADDRESS
    private static final int SERVER_PORT = 5000;

    private BufferedReader in;
    private PrintWriter out;

    private JFrame frame = new JFrame("Chat Client");
    private JTextArea messageArea = new JTextArea(20, 50);
    private JTextField inputField = new JTextField(40);
    private JButton sendButton = new JButton(">");


    public ClientGUI(String serverAddress, int port) {
        try {
            Socket socket = new Socket(serverAddress, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        }

        catch (IOException e) {
            System.out.println("Couldn't connect to server");
            return;
        }

        // GUI
        messageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageArea);

        JPanel panel = new JPanel();
        panel.add(inputField);
        panel.add(sendButton);

        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(panel, BorderLayout.SOUTH);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    messageArea.append(line + "\n");
                }
            }
            catch (IOException e) {
                messageArea.append("Disconnected.\n");
            }
        }).start();
    }

    private void sendMessage() {
        String message = inputField.getText();
        if (message.length() > 0) {
            out.println(message);
            messageArea.append("Me: " + message + "\n");
            inputField.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientGUI(SERVER_ADDRESS, SERVER_PORT));
    }
}
