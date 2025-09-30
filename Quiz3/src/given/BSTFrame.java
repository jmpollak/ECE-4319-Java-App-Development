package given;

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

public class BSTFrame extends JFrame {
    /**
     * Member variables
     */
    private BSTPanel bstPanel;
    private InsertBST bst;
    private JTextField inputField;
    private JButton insertButton;

    private HashSet<Integer> uniqueNumberSet; // we can use a HashSet to make sure numbers are unique

    /**
     * Constructor of BSTFrame
     */
    public BSTFrame() {
        //set frame layout to BorderLayout
        setLayout(new BorderLayout());

        // Handling non-duplicated numbers
        uniqueNumberSet = new HashSet<>();

        // Initialize BST
        bst = new InsertBST();

        // BST panel at center
        bstPanel = new BSTPanel();
        add(bstPanel, BorderLayout.CENTER);

        // Input panel on top
        JPanel inputPanel = new JPanel();
        inputField = new JTextField(10);
        insertButton = new JButton("Insert");

        inputPanel.add(new JLabel("Enter number:"));
        inputPanel.add(inputField);
        inputPanel.add(insertButton);
        add(inputPanel, BorderLayout.NORTH);


        // Registering the "Button ActionEvent" handler
        ButtonHandler handler = new ButtonHandler();
        insertButton.addActionListener(handler);

    }

    private class ButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int num = Integer.parseInt(inputField.getText());
                if (uniqueNumberSet.size() >= 10) {
                    JOptionPane.showMessageDialog(null,
                            "BST already has 10 unique numbers!");
                    return;
                }

                if (uniqueNumberSet.contains(num)) {
                    JOptionPane.showMessageDialog(null,
                            "Duplicate number! Enter a unique value.");
                    return;
                }

                uniqueNumberSet.add(num);
                bst.insert(num);
                bstPanel.setRootAndDraw(InsertBST.root);
                inputField.setText("");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null,
                        "Please enter a valid integer.");
            }
        }
    }

}
