package Data_Structure;

import java.util.ArrayList;
import java.util.List;

class BSTInorder {
    public static void main (String[] args) {
        BSTInorder quiz = new BSTInorder();
        TreeNode node = new TreeNode(3);
        node.left = new TreeNode(1);
        node.right = new TreeNode(5);
        node.left.left = new TreeNode(0);
        node.left.right = new TreeNode(2);
        node.right.left = new TreeNode(4);

        quiz.inorderTraversal(node);

        System.out.println("\nThe final Array List after the recursion: " + res);
    }

    public static List<Integer> res;
    public void inorderTraversal(TreeNode root) {
        res = new ArrayList<>();
        inOrder(root);

    }

    public void inOrder(TreeNode root){
        // exit of the recursion
        if(root == null) return;

        // breaks down to a simpler problem
        inOrder(root.left);     // let recursion handle left subtree
        res.add(root.val);      //
        System.out.print(root.val + ", ");
        inOrder(root.right);    // let recursion handle right subtree

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
