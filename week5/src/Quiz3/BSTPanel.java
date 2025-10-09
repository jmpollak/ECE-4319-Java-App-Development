package Quiz3;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
// Used to draw the BST
class BSTPanel extends JPanel {
    public InsertBST.TreeNode root;

    //Used for setting the root and then updating the panel
    public void setRootAndDraw(InsertBST.TreeNode root) {
        this.root = root;
        repaint();
    }

    /**
     * Drawing of the Binary Search Tree
     * <pre>
     * The BST needs to be done in the format of Nodes and Links.
     * We are asked to do it using {@code drawOval()} and {@code drawLine()}.
     *
     * It is a hierarchical tree where the root is on top and
     * the rest cascades down branching with either right or
     * left branches.
     *
     * To achieve this we need to have it broken into a
     * constant y changing of pixels (ex 50) and we need
     * an offset of the x in relation to the root node.
     *
     * Something to note is the order of drawing. If the
     * nodes are drawn first or created first in the code
     * then the lines will be on top of the node. </pre>
     */
    private void drawTree(Graphics g, InsertBST.TreeNode node, int x, int y, int
            xOffset) {
// Draw the lines
        g.setColor(Color.BLACK);
        if (node.left != null) {
            g.drawLine(x, y, x - xOffset, y + 60);
            drawTree(g, node.left, x - xOffset, y + 60, xOffset / 2);
        }
        if (node.right != null) {
            g.drawLine(x, y, x + xOffset, y + 60);
            drawTree(g, node.right, x + xOffset, y + 60, xOffset / 2);
        }
// Draw nodes
        g.setColor(Color.orange);
// (x, y) will be at the center of the node
        g.fillOval(x - 20, y - 20, 40, 40);
        g.setColor(Color.BLACK);
        g.drawOval(x - 20, y - 20, 40, 40);
// Draw value
        g.drawString(String.valueOf(node.val), x - 6, y + 5);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.WHITE);
        if (root != null) {
            drawTree(g, root, getWidth() / 2, 50, getWidth() / 4);
        }
    }
}