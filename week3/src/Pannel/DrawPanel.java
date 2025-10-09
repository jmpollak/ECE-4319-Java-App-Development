package Pannel;
import java.awt.*;
import javax.swing.JPanel;
//  Using drawLine to connect the corners of a panel.
public class DrawPanel extends JPanel
{
    // draws an X from the corners of the panel
    public void paintComponent( Graphics g )
    {
        // call paintComponent to ensure the panel displays correctly
        super.paintComponent( g );

        int width = getWidth(); // total width
        int height = getHeight(); // total height
        g.setColor(Color.RED);
        // draw a line from the upper-left to the lower-right
        g.drawLine( 0, 0, width, height );

        g.setColor(Color.green);
        // draw a line from the lower-left to the upper-right
        g.drawLine( 0, height, width, 0 );

    } // end method paintComponent
} // end class DrawPanel