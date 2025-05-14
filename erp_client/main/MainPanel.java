package main;

import com.google.gson.*;
import java.util.Date;

import java.text.SimpleDateFormat;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import java.io.InputStreamReader;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowStateListener;
import java.awt.event.WindowEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Color;

import java.io.File;
import java.io.InputStream;

import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import javax.swing.SwingUtilities;

import static javax.swing.JOptionPane.showMessageDialog;

import main.pages.IPage;
import main.helpers.Menu;

class ResponseTimeJson {
    public String time;
    public String toString() {
        return "Time: " + time;
    }
}


/**
 * MainPanel is set to the main frame and show side menu with header info, allows to set innerPanel.
 *
 * @author Alexander Smirnov
 * @version 2025-01-08
 */
public class MainPanel extends JPanel {
    public MainPanel(JFrame frame, JPanel cards, List<MenuItem> menus, String[] credentials) {
        super();
        JPanel self = this;
        this.setBackground(Color.LIGHT_GRAY);
        
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        Menu.addNodes(root, menus);
        JTree tree = new JTree(root);
        tree.setRootVisible(false);
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

        MouseListener ml = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int selRow = tree.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                if (selRow != -1) {
                    MenuItem menuItem = (MenuItem)((DefaultMutableTreeNode)selPath.getLastPathComponent()).getUserObject();
                    if (e.getClickCount() == 1) {
                        int tabCount = tabbedPane.getTabCount();
                        for (int i = 0; i < tabCount; i++) {
                            String tabTitle = tabbedPane.getTitleAt(i);
                            if (tabTitle.equals(menuItem.name)) {
                                tabbedPane.setSelectedIndex(i);
                                break;
                            }
                        }
                    } else if (e.getClickCount() == 2) {
                        try {
                            Class<?> clazz = Class.forName(menuItem.pane_class);
                            IPage page = (IPage)(clazz.newInstance());
                            JPanel p = page.getPane();
                            tabbedPane.add(p);
                            tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(p), JTabbedPaneWithCloseButton.getTitlePanel(tabbedPane, p, menuItem.name));
                            tabbedPane.setTitleAt(tabbedPane.indexOfComponent(p), menuItem.name);
                            tabbedPane.setSelectedIndex(tabbedPane.indexOfComponent(p));
                        } catch (Exception ex) {
                            System.out.println("Класс не найден: " + ex.getMessage());
                        }
                    }
                }
            }
        };
        tree.addMouseListener(ml);

        JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        JScrollPane scrollPane = new JScrollPane(splitter);
        JPanel headerPane = new JPanel();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        
        /*
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.add(new JLabel("_blank"));
        tabbedPane.add(p);
        tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(p), JTabbedPaneWithCloseButton.getTitlePanel(tabbedPane, p, "_blank"));
        
        JPanel p2 = new JPanel();
        p2.setOpaque(false);
        p2.add(new JLabel("haha"));
        tabbedPane.add(p2);
        tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(p2), JTabbedPaneWithCloseButton.getTitlePanel(tabbedPane, p2, "haha"));
        
        JPanel p3 = new JPanel();
        p3.setOpaque(false);
        p3.add(new JLabel("musor"));
        tabbedPane.add(p3);
        tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(p3), JTabbedPaneWithCloseButton.getTitlePanel(tabbedPane, p3, "musor"));
        */

        tabbedPane.setPreferredSize(new Dimension(400, 400));
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                Dimension size = e.getComponent().getSize();
                size.height -= 10 + headerPane.getSize().height;
                size.width -= 10;
                scrollPane.setPreferredSize(size);
            }
            public void componentShown(ComponentEvent e) {
                Dimension size = e.getComponent().getSize();
                size.height -= 10 + headerPane.getSize().height;
                size.width -= 10;
                scrollPane.setPreferredSize(size);
            }
        });

        /*
        frame.addWindowStateListener(new WindowStateListener() {
            public void windowStateChanged(WindowEvent e) {
                Dimension size = e.getWindow().getSize();
                size.height -= 10 + headerPane.getSize().height;
                size.width -= 10;
                scrollPane.setPreferredSize(size);
                frame.pack();
                System.out.println("Dzz");
            }
        });
        */

        splitter.setLeftComponent(tree);
        splitter.setRightComponent(tabbedPane);
        
        headerPane.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        JLabel groupLbl = new JLabel(credentials[1]);
        groupLbl.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
        headerPane.add(groupLbl);
        JLabel nameLbl = new JLabel(credentials[0]);
        nameLbl.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
        headerPane.add(nameLbl);
        JButton ringBtn = new JButton();
        ringBtn.setIcon(new ImageIcon("resources/ring.png"));
        headerPane.add(ringBtn);
        ringBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { 
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(self);
                GotNotificationPanel gotNotifications = new GotNotificationPanel(topFrame);
            }
        });
        try {
            int cnt = ServerService.getInstance().getNotificationCount();
            if (cnt > 0) {
                ringBtn.setText("(" + cnt + ")");
            }
        } catch (Exception ex) {
            showMessageDialog(null, "Ouch! " + ex.getMessage());
        }
        final AtomicBoolean running = new AtomicBoolean(false);
        final Thread th = new Thread(() -> {
            running.set(true);
            try {
                String host = ServerService.getInstance().env.get("TCP_HOST");
                int port =  Integer.parseInt(ServerService.getInstance().env.get("TCP_PORT"));

                Socket socket = new Socket(host, port);

                InputStream input = socket.getInputStream();
                InputStreamReader reader = new InputStreamReader(input);
     
                int character;
                StringBuilder data = new StringBuilder();

                String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                do {
                    data.setLength(0);
                    while (running.get() && (character = reader.read()) != -1 && character != (int)'}') {
                        data.append((char) character);
                    }
                    if (running.get()) {
                        data.append('}');
                        Gson gson = new Gson();
                        ResponseTimeJson responseJson = gson.fromJson(data.toString(), ResponseTimeJson.class);
                        String newTime = responseJson.time.substring(0, 19);
                        if (newTime.compareTo(currentTime) > 0) {
                            currentTime = newTime;
                            try {
                                int cnt = ServerService.getInstance().getNotificationCount();
                                if (cnt > 0) {
                                    ringBtn.setText("(" + cnt + ")");
                                } else {
                                    ringBtn.setText("");
                                }
                            } catch (Exception ex) {
                                showMessageDialog(null, "Ouch! " + ex.getMessage());
                            }
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println("Thread interrupted");
                    }
                } while (running.get());
            } catch (Exception ex) {
                showMessageDialog(null, "Ouch! " + ex.getMessage());
            }
        });
        th.start();
        JButton logoutBtn = new JButton("Выйти");
        JPanel current = this;
        logoutBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { 
                try {
                    running.set(false);
                    cards.remove(current);
                    ServerService.getInstance().logout();
                    CardLayout cl = (CardLayout)(cards.getLayout());
                    cl.show(cards, "loginPane");
                } catch (Exception ex) {
                    showMessageDialog(null, "Ouch! " + ex.getMessage());
                }
            }
        });
        headerPane.add(logoutBtn);
        add(headerPane, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
}
