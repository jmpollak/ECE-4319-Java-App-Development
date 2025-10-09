package Quiz3;

import java.util.ArrayList;
import java.util.List;

public class InsertBST {
    public static TreeNode root;
    public static List<Integer> res;

    public static void main (String[] args) {
        InsertBST insertion = new InsertBST();
        int[] listOfNodes = {5,3,6,7,2,9,1};
        for (int i = 0; i < listOfNodes.length; i++) {
            insertion.insert(listOfNodes[i]);
        }

        insertion.inorderTraversal(root);
        System.out.println(res);
    }

    /**
     * Inserting a BST with a single TreeNode's value at a time
     * @param value
     */
    public void insert(int value) {
        root = insertRecursive(root, value); // always assigns the "actual root" to this root variable
    }

    private TreeNode insertRecursive(TreeNode current, int value) {
        // If the tree is empty, the new node becomes the root
        if (current == null) {
            return new TreeNode(value);
        }

        if (value < current.val) {
            current.left = insertRecursive(current.left, value);
        } else if (value > current.val) {
            current.right = insertRecursive(current.right, value);
        } else {
            // value already exists
            return current;
        }

        return current;
    }


    /**
     * In-order traversal for BST, to print all the nodes in BST on screen
     * @param root
     */
    public void inorderTraversal(TreeNode root) {
        res = new ArrayList<>();
        inOrder(root);

    }

    private void inOrder(TreeNode root){
        // exit of the recursion
        if(root == null) return;

        // breaks down to a simpler problem
        inOrder(root.left);     // let recursion handle left subtree
        res.add(root.val);      //
        System.out.print(root.val + ", ");
        inOrder(root.right);    // let recursion handle right subtree
    }

    public class TreeNode {
        public int val;
        public TreeNode left;
        public TreeNode right;
        public TreeNode(int x) {
            val = x;
        }
    }
}