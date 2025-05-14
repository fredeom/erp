package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Write a description of class JTabbedPaneWithCloseButton here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class JTabbedPaneWithCloseButton {
    public static JPanel getTitlePanel(final JTabbedPane tabbedPane, final JPanel panel, String title) {
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);
        JLabel titleLbl = new JLabel(title);
        titleLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        titlePanel.add(titleLbl);
        JPanel closeButton = new JPanel() {
               @Override
               public void paintComponent(Graphics g) {
                   Graphics2D g2 = (Graphics2D) g;
                   g2.setColor(Color.BLACK);
                   g2.drawLine(1, 1, 9, 9);
                   g2.drawLine(9, 1, 1, 9);
                   g2.drawRect(1, 1, 8, 8);
               }
        };
     
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tabbedPane.remove(panel);
            }
        });
        titlePanel.add(closeButton);
        return titlePanel;
    }
}