package created;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

//Contains all the interactable and the layout of the panel
public class BSTFrame extends JFrame
{
    // Formating and Interaction
    private BSTPanel bstPanel;
    private JTextField inputField;
    private JButton insertButton;

    // Variables
    private InsertBST bst;
    private HashSet<Integer> uniqueNumberSet;

    /**<pre>
     * Setting the Layout to Boarder Layout
     * Want to have it set up where the input panel is at the {@code SOUTH}
     * and the bst drawing panel is at {@code CENTER }
     *
     * TODO:
     *  * Set the layout of the Panel
     *  * Initialize the variables
     *  * Create an action event for the button
     *</pre>
     */
    public BSTFrame()
    {
       //Setting the Layout
        setLayout(new BorderLayout());

        // Initialize BST
        bst = new InsertBST();

        // Initialize HashSet numbers
        uniqueNumberSet = new HashSet<>();

        // BST Panel in the Center
        bstPanel = new BSTPanel();
        add(bstPanel, BorderLayout.CENTER);

        // Input panel at the bottom
        JPanel inputPanel = new JPanel();
        // Need to initialize the items inside the Panel
        inputField = new JTextField(10);
        insertButton = new JButton("Insert");

        /*Need create the layout of the panel by placing all
          the items inside the panel
          Want it to look like
          "Enter Number:" [inputField] [inputButton] */
        inputPanel.add(new JLabel("Enter Number:"));
        inputPanel.add(inputField);
        inputPanel.add(insertButton);
        add(inputPanel, BorderLayout.SOUTH);

        // Initialize the button handler
        ButtonHandler handler = new ButtonHandler();
        insertButton.addActionListener(handler);
    }
    /** Create the Button Action Event Handler
     * <pre>
     * The button handler needs to do a few different actions:
     *
     *  1. Insert the number into the BST and check it is a number
     *
     *  2. It needs to do some error checking to make sure it does
     *     not insert the same number twice.
     *
     *  3. Display an error message if the input was incorrect.
     * </pre>
     */
    private class ButtonHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            try // Error detection
            {
                int number = Integer.parseInt(inputField.getText());

                //Using the HashSet to set a size limit
                if(uniqueNumberSet.size() >= 10)
                {
                    JOptionPane.showMessageDialog(null, "BST contains 10 unique numbers");
                    return;
                }
                // Checking for Unique numbers with hashset
                if (uniqueNumberSet.contains(number))
                {
                    JOptionPane.showMessageDialog(null, number+" has been used.\nEnter a unique number");
                    return;
                }

                //Enter the numbers into BST and hashset
                uniqueNumberSet.add(number);
                bst.insert(number);
                bstPanel.setRootAndDraw(InsertBST.root);
                inputField.setText("");
            }
            catch(NumberFormatException ex)
            {
                JOptionPane.showMessageDialog(null, "Please enter a valid number");
            }
        }
    }
}
