package main;

import java.util.List;
import java.util.ArrayList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import static javax.swing.JOptionPane.showMessageDialog;

import main.ServerService;
import main.MenuItem;

import main.records.Notification;

/**
 * There are notifications and ability to hide them from the list.
 *
 * @author Alexander Smirnov
 * @version 2025-05-11
 */
public class GotNotificationPanel extends JDialog {
    public GotNotificationPanel(JFrame frame) {
        super(frame, "Уведомления", true);
        getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.gridx = 0;
        c.gridy = 0;
        JButton leftBtn = new JButton("<");
        getContentPane().add(leftBtn, c);
        c.gridx = 1;
        int anch = c.anchor;
        c.anchor = GridBagConstraints.WEST;
        JButton rightBtn = new JButton(">");
        getContentPane().add(rightBtn, c);
        c.anchor = anch;
        c.gridy++;
        c.gridx = 0;
        c.gridwidth = 2;
        JTextArea ta = new JTextArea(8, 25);
        getContentPane().add(ta, c);
        c.gridy++;
        c.gridx = 0;
        JButton seenBtn = new JButton("Отметить как просмотренное");
        getContentPane().add(seenBtn, c);
        final Map<String, Integer> currentIndex = new HashMap<String, Integer>();
        currentIndex.put("index", 0);
        
        List<Notification> notifications = new LinkedList<Notification>();
        try {
            notifications = ServerService.getInstance().getNotifications();
            if (notifications.size() > 0) {
                ta.setText(notifications.get(0).text);
            }
        } catch (Exception ex) {
            showMessageDialog(null, "Ouch! " + ex.getMessage());
        }
        final List<Notification> notifs = notifications;
        leftBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                currentIndex.put("index", (currentIndex.get("index") + notifs.size() - 1) % notifs.size());
                if (notifs.size() > 0) {
                    ta.setText(notifs.get(currentIndex.get("index")).text);
                }
            }
        });
        rightBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (notifs.size() > 0) {
                    currentIndex.put("index", (currentIndex.get("index") + notifs.size() + 1) % notifs.size());
                    ta.setText(notifs.get(currentIndex.get("index")).text);
                }
            }
        });
        seenBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (notifs.size() > 0) {
                    try {
                        ServerService.getInstance().notificationSeen(notifs.get(currentIndex.get("index")).id);
                    } catch (Exception ex) {
                        showMessageDialog(null, "Ouch! " + ex.getMessage());
                    }
                    notifs.remove(notifs.get(currentIndex.get("index")));
                    if (notifs.size() > 0) {
                        currentIndex.put("index", (currentIndex.get("index") + notifs.size()) % notifs.size());
                        ta.setText(notifs.get(currentIndex.get("index")).text);
                    } else {
                        ta.setText("Нет новых уведомлений!");
                    }
                }
            }
        });

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
