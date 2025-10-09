package Quiz3;

import javax.swing.*;
/**Requirements:
 * <pre>
 * 1. Your program should provide a Swing-based GUI, using {@code JFrame} and {@code JPanel}.
 * The user can:

 * * Enter integers (as node values) in a text field such as {@code JTextField} or
 *   {@code JOptionPane.showInputDialog}.

 * * A button (e.g., “Insert”) should insert the user’s number into the BST.

 *  * Hint: in your {@code JFrame}, try setting the layout to {@code BorderLayout} instead of
 *    {@code FlowLayout}.

 * 2. Each integer will be inserted into the BST according to the BST insertion
 *    rules.

 * Once user inputs everything, the BST should be visually displayed on a {@code JPanel}.

 * Nodes should be drawn as circles with the integer value inside. Use methods
 * such as {@code g.drawOval()}.

 * Links (edges) should be drawn as lines connecting parent nodes to child nodes.
 * Such as {@code g.drawLine()}.

 * Alternatively, you can display the tree dynamically on the panel as numbers are inserted.

 * After the above version, try limiting user inputs to 10 unique integers (no duplicates allowed).

 * Deliverables:

 *  1. Print screen and submit your screenshot to the activity

 *  2. Example input/output:
 *      If the user enters: 50, 30, 70, 20, 40, 60, 80
 *      The GUI should show a BST with 50 as the root, 30 and 70 as children,
 *      etc., with circles and lines.
 *
 *      This was made and by following the provided slides</pre>*/
public class Main
{
    public static void main(String[] args)
    {
        BSTFrame frame = new BSTFrame();
        frame.setTitle("Binary Search Tree");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
