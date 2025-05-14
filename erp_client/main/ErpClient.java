package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.CardLayout;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ContainerListener;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;

import javax.swing.JTabbedPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

/**
 * ErpClient is responsible for starting app.
 *
 * @author Alexander Smirnov
 * @version 2025-01-03
 */
public class ErpClient
{
    public void addComponentToPane(JFrame frame) {
        Container pane = frame.getContentPane();
        JPanel cards = new JPanel(new CardLayout());
        cards.setBackground(Color.GREEN);
        cards.add(new LoginPane(frame, cards), "loginPane");
        pane.add(cards, BorderLayout.CENTER);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("ERP Client v0.0.1");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            ErpClient erpClient = new ErpClient();
            erpClient.addComponentToPane(frame);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
