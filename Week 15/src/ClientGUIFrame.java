import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

public class ClientGUIFrame
{
    private static int PORT = 8083;
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField inputField;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;
    private String username;

    public void startClient()
    {
        username = JOptionPane.showInputDialog("Enter your username:");

        if (username == null || username.trim().isEmpty())
        {
            username = "Anonymous-" + UUID.randomUUID();
        }

        frame = new JFrame("Chat - " + username);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);

        inputField = new JTextField();
        frame.add(inputField, BorderLayout.SOUTH);

        inputField.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String message = inputField.getText().trim();
                if(!message.isEmpty())
                {
                    out.println(username + ": " + message);
                    inputField.setText("");
                }
            }
        });

        frame.setVisible(true);
        connectToServer();
    }

    private void connectToServer()
    {
        try
        {
            socket = new Socket("localhost", PORT); // Connect through IP and socket
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Thread to listen for incoming message
            Thread readerThread = new Thread(() -> // Use multiple Threads to read message from server
            {
                try
                {
                    String serverMessage;
                    while((serverMessage = in.readLine()) != null)
                    {
                        chatArea.append(serverMessage + "\n");
                    }
                }
                catch (IOException e)
                {
                    chatArea.append("Disconnected from Server.\n");
                }
            });
            readerThread.start();

            chatArea.append("Connected to chat server.\n");
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(frame, "Unable to connect to server.", "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
}
