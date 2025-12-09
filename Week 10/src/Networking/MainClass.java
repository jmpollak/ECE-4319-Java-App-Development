package Networking;

import javax.swing.JFrame;

public class MainClass {
    public static void main(String[] args) {
        ReadServerFile application = new ReadServerFile();
        application.setSize(600, 800);
        application.setLocationRelativeTo(null);
        application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
