package given;

import javax.swing.*;


// Main GUI application
public class VisualizedBSTApp {

    public static void main(String[] args) {
        BSTFrame frame = new BSTFrame();
        frame.setTitle("Binary Search Tree Visualizer");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

