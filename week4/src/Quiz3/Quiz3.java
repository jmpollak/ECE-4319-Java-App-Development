package Quiz3;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Quiz3 extends JFrame {

    // 1) Core BST (InsertBST)
    static class InsertBST
    {
        static class TreeNode
        {
            int val;
            TreeNode left, right;
            TreeNode(int x) { val = x; }
        }

        TreeNode root;
        int size = 0;
        int LIMIT = 10;

        boolean insert(int x)
        {
            if (size >= LIMIT) return false;

            if (root == null)
            {
                root = new TreeNode(x);
                size++;
                return true;
            }
            boolean ok = insertRec(root, x);
            if (ok) size++;
            return ok;
        }
        //Recursive behavior
        private boolean insertRec(TreeNode cur, int x)
        {
            if (x == cur.val)
            {
                return false; // no duplicates
            }
            // Left node
            if (x < cur.val)
            {
                if (cur.left == null)
                {
                    cur.left = new TreeNode(x);
                    return true;
                }
                return insertRec(cur.left, x);
            }
            else // Right Node
            {
                if (cur.right == null)
                {
                    cur.right = new TreeNode(x);
                    return true;
                }
                return insertRec(cur.right, x);
            }
        }
    }

    // 2) Panel that DRAWS the BST
    static class TreePanel extends JPanel
    {
        InsertBST bst;
        int R = 18;          // node radius
        int LEVEL_GAP = 70;  // vertical spacing

        TreePanel(InsertBST bst)
        {
            this.bst = bst;
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            if (bst.root == null)
            {
                return;
            }

            int w = Math.max(getWidth(), 800);
            int startX = w / 2;
            int startY = 60;
            int xOffset = Math.max(40, w / 4);

            // Draw lines first so circles sit on top
            drawEdges(g, bst.root, startX, startY, xOffset);
            // Then draw circles + labels
            drawNodes(g, bst.root, startX, startY, xOffset);
        }

        private void drawEdges(Graphics g, InsertBST.TreeNode n, int x, int y, int xOff)
        {
            if (n == null)
            {
                return;
            }
            if (n.left != null)
            {
                int cx = x - xOff, cy = y + LEVEL_GAP;
                g.drawLine(x, y, cx, cy);
                drawEdges(g, n.left, cx, cy, Math.max(20, xOff / 2));
            }
            if (n.right != null)
            {
                int cx = x + xOff, cy = y + LEVEL_GAP;
                g.drawLine(x, y, cx, cy);
                drawEdges(g, n.right, cx, cy, Math.max(20, xOff / 2));
            }
        }

        private void drawNodes(Graphics g, InsertBST.TreeNode n, int x, int y, int xOff)
        {
            if (n == null)
            {
                return;
            }

            if (n.left != null)
            {
                drawNodes(g, n.left,  x - xOff, y + LEVEL_GAP, Math.max(20, xOff / 2));
            }
            if (n.right != null)
            {
                drawNodes(g, n.right, x + xOff, y + LEVEL_GAP, Math.max(20, xOff / 2));
            }

            int d = R * 2;
            g.drawOval(x - R, y - R, d, d);
            // Value inside the circle
            String s = String.valueOf(n.val);
            FontMetrics fm = g.getFontMetrics();
            int tx = x - fm.stringWidth(s) / 2;
            int ty = y + fm.getAscent() / 2 - 2;
            g.drawString(s, tx, ty);
        }
    }

    // 3) JFrame + GUI wiring (the application)
    private InsertBST bst = new InsertBST();
    private TreePanel treePanel = new TreePanel(bst);
    private JTextField inputField = new JTextField();
    private JLabel status = new JLabel("Enter an integer or a comma-separated list, then press Enter. (Max 10 unique)");

    public Quiz3()
    {
        super("BST Visualizer Quiz 3");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLayout(new BorderLayout(8, 8));

        JPanel north = new JPanel(new BorderLayout(8, 8));
        north.add(new JLabel(" Input: "), BorderLayout.WEST);
        north.add(inputField, BorderLayout.CENTER);
        add(north, BorderLayout.NORTH);

        add(treePanel, BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        south.add(status, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        inputField.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // Runs when the user presses Enter in the text field
                insertFromText(inputField.getText().trim());
            }
        });

        setLocationRelativeTo(null);
    }

    // Generic warning popup
    private void warn(String message)
    {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Input Warning",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void insertFromText(String rawText)
    {
        String text = rawText == null ? "" : rawText.trim();

        if (text.isEmpty())
        {
            warn("Please type an integer (or a comma/space-separated list).");
            return;
        }

        boolean hadInvalid = false;
        boolean hadDuplicate = false;
        boolean hitCap = false;

        // Split on commas or any whitespace
        String[] tokens = text.split("[,\\s]+");

        for (String tok : tokens)
        {
            if (tok == null || tok.isEmpty()) continue;

            int v;
            try
            {
                v = Integer.parseInt(tok);
            } catch (NumberFormatException ex)
            {
                hadInvalid = true;
                continue;
            }

            if (bst.size >= bst.LIMIT)
            {
                hitCap = true;
                break;
            }
            boolean ok = bst.insert(v);
            if (!ok)
            {
                hadDuplicate = true;
            }
        }

        treePanel.repaint();
        inputField.setText("");

        // Update bottom bar: only Remaining / Limit reached
        int remaining = Math.max(0, bst.LIMIT - bst.size);
        if (bst.size >= bst.LIMIT)
        {
            status.setText("Limit reached (" + bst.LIMIT + ").");
            inputField.setEditable(false);
            inputField.setEnabled(false);
        }
        else
        {
            status.setText("Remaining: " + remaining);
        }

        // Show a single generic popup if anything went wrong
        if (hadInvalid || hadDuplicate || hitCap)
        {
            StringBuilder w = new StringBuilder("Some inputs were not inserted:\n");
            if (hadInvalid)
            {
                w.append(" • Some values were not valid integers.\n");
            }
            if (hadDuplicate)
            {
                w.append(" • Some duplicates were ignored.\n");
            }
            if (hitCap || bst.size >= bst.LIMIT)
            {
                w.append(" • Limit reached (").append(bst.LIMIT).append(").");
            }
            warn(w.toString());
        }
    }

    public static void main(String[] args)
    {
        new Quiz3().setVisible(true);
    }
}