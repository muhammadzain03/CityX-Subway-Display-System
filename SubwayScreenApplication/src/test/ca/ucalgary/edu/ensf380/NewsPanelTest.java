package test.ca.ucalgary.edu.ensf380;

import ca.ucalgary.edu.ensf380.controller.NewsController;
import ca.ucalgary.edu.ensf380.view.NewsPanel;

import javax.swing.*;
import java.awt.*;

public class NewsPanelTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("News Panel Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 100);

        NewsPanel newsPanel = new NewsPanel();
        NewsController newsController = new NewsController(newsPanel);

        frame.getContentPane().add(newsPanel.getPanel(), BorderLayout.CENTER);
        frame.setVisible(true);

        // Argument for News
        newsController.retrieveNews("CA");
    }
}
