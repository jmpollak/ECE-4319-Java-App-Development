import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;

public class ReadServerFile extends JFrame
{
    private JTextField enterField;
    private JEditorPane webpagePane;

    // Set up GUI
    public ReadServerFile()
    {
        enterField = new JTextField("Enter file URL here");
        System.setProperty("http.agent","Mozilla/5.0");
        enterField.addActionListener(new ActionListener()
                 {
                     @Override
                     public void actionPerformed(ActionEvent event)
                     {
                         getThePage(event.getActionCommand());
                     }
                 });
        add(enterField, BorderLayout.NORTH);

        webpagePane = new JEditorPane();
        webpagePane.setEditable(false);
        webpagePane.addHyperlinkListener(new HyperlinkListener()
        {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent event)
            {
                if(event.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
                {
                    getThePage(event.getURL().toString());
                }
            }
        });
        add(new JScrollPane(webpagePane), BorderLayout.CENTER);
        setVisible(true); // Shows the window

    }

    private void getThePage(String location)
    {
        try
        {
            webpagePane.setPage(location);
            enterField.setText(location);

        }
        catch (MalformedURLException e)
        {
            JOptionPane.showMessageDialog(this,
                    "Error retrieving specified URL",
                    "Bad URL",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(this,
                    "IOException occured",
                    "File Not Found",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}