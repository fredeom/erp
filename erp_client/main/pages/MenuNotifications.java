package main.pages;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Point;
import javax.swing.DefaultListModel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextArea;

import main.ServerService;
import main.records.UserGroup;
import main.records.UserShort;
import main.records.User;

/**
 * MenuNotification allows to send notifiications to all.
 *
 * @author Alexander Smirnov
 * @version 2025-05-10
 */
public class MenuNotifications extends JPanel implements IPage {
    @Override
    public JPanel getPane() {
        JPanel self = this;
        setOpaque(false);
        setLayout(new GridBagLayout());
        
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.gridx = 0;
        c.gridy = 0;
        JTextArea ta = new JTextArea(8, 25);
        add(ta, c);
        c.gridy++;
        JButton addBtn = new JButton("Послать");
        add(addBtn, c);
        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    ServerService.getInstance().sendNotification(ta.getText());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        });
        return this;
    }
}
