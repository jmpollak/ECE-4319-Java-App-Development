package given;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;

// Panel to draw the BST
class BSTPanel extends JPanel {
    public InsertBST.TreeNode root;

    public void setRootAndDraw(InsertBST.TreeNode root) {
        this.root = root;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.WHITE);
        if (root != null) {
            drawTree(g, root, getWidth() / 2, 50, getWidth() / 4);
        }
    }

    private void drawTree(Graphics g, InsertBST.TreeNode node, int x, int y, int xOffset) {
        g.setColor(Color.BLACK);
        if (node.left != null) {
            g.drawLine(x, y, x - xOffset, y + 60);
            drawTree(g, node.left, x - xOffset, y + 60, xOffset / 2);
        }
        if (node.right != null) {
            g.drawLine(x, y, x + xOffset, y + 60);
            drawTree(g, node.right, x + xOffset, y + 60, xOffset / 2);
        }

        // Draw node as circle
        g.setColor(Color.yellow);
        // (x, y) will be at the center of the oval
        g.fillOval(x - 20, y - 20, 40, 40);
        g.setColor(Color.BLACK);
        g.drawOval(x - 20, y - 20, 40, 40);

        // Draw value
        g.drawString(String.valueOf(node.val), x - 6, y + 5);
    }
}
