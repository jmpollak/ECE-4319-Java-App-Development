package DataStructure;

/*
 * Questions: Given the root of a binary tree, determine if it is a valid binary
 search tree (BST).
 *
 * A valid BST is defined as follows:
 *
 * The left subtree of a node contains only nodes with keys strictly less than the
 node's key.
 * The right subtree of a node contains only nodes with keys strictly greater than
 the node's key.
 * Both the left and right subtrees must also be binary search trees.
 *
 * Example 1:
 *. 2
 * 1 3
 * Input: root = [2,1,3]
 * Output: true
 *
 *
 * Example 2:
 *. 5
 * 1 4
 * 3 6
 * Input: root = [5,1,4,null,null,3,6]
 * Output: false
 *
 * Example 3:
 * 3
 * 1 5
 * 0 2 4
 *
 * Input: root = [3, 1, 5, 0, 2, 4, null]
 * Output: true
 * Explanation: The root node's value is 5 but its right child's value is 4.
 */

class Quiz2 {
    public static void main (String[] args) {
        Quiz2 quiz = new Quiz2();
        TreeNode node = new TreeNode(3);
        node.left = new TreeNode(1);
        node.right = new TreeNode(5);
        node.left.left = new TreeNode(0);
        node.left.right = new TreeNode(2);
        node.right.left = new TreeNode(4);
        //Example 1
        TreeNode node1 = new TreeNode(2);
        node1.left = new TreeNode(1);
        node1.right = new TreeNode(3);
        //Example 2
        TreeNode node2 = new TreeNode(5);
        node2.left = new TreeNode(1);
        node2.right = new TreeNode(4);
        node2.right.left = new TreeNode(3);
        node2.right.right = new TreeNode(6);
        //Example 3
        TreeNode node3 = new TreeNode(3);
        node3.left = new TreeNode(1);
        node3.right = new TreeNode(5);
        node3.left.left = new TreeNode(0);
        node3.left.right = new TreeNode(2);
        node3.right.left = new TreeNode(4);
        //Example 4
        TreeNode node4 = new TreeNode(8);
        node4.left = new TreeNode(6);
        node4.right = new TreeNode(12);
        node4.left.left = new TreeNode(4);
        node4.left.right = new TreeNode(7);
        node4.right.left = new TreeNode(13);
        node4.right.right = new TreeNode(9);
        //Testing
        quiz.isValidBST(node);
        System.out.println( "Given example is "+ quiz.isValidBST(node) + " with an input of [3,1,5,0,2,4]");
        System.out.println( "Example 1 is "+ quiz.isValidBST(node1)+ " with an input of [2,1,3]");
        System.out.println( "Example 2 is "+ quiz.isValidBST(node2)+ " with an input of [5,1,4,null,null,3,6]");
        System.out.println( "Example 3 is "+ quiz.isValidBST(node3)+ " with an input of [3,1,5,0,2,4,null]");
        System.out.println( "Example 4 is "+ quiz.isValidBST(node4)+ " with an input of [8,6,12,4,7,13,9]");
    }

    public boolean isValidBST(TreeNode root) {
        return validate(root, null, null);
    }
    // Checks every node's value by looking at their upper and lower bounds
    private boolean validate(TreeNode node, Integer lower, Integer upper)
    {
        if (node == null) return true; //Checking to make sure there is a value

        if (lower != null && node.val <= lower) return false; // must be strictly greater than lower bound
        if (upper != null && node.val >= upper) return false; // must be strictly less than the upper bound

        return validate(node.left, lower, node.val)   // left subtree < node.val
            && validate(node.right, node.val,upper);  // right subtree > node.val
    }
    //Definition for a binary tree node.
    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int x) {
            val = x;
        }
    }
}
