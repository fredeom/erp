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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import main.ServerService;
import main.records.UserGroup;
import main.records.UserShort;
import main.records.User;

/**
 * Users's info will be stored in this class.
 *
 * @author Alexander Smirnov
 * @version 2025-02-23
 */
public class MenuUsers extends JPanel implements IPage {
    //public int id;
    //public String login;
    //public String pass;
    //public String name;
    //public String phone;
    //public int userGroupId;
    //public Users() {}
    @Override
    public JPanel getPane() {
        JPanel self = this;
        setOpaque(false);
        setLayout(new GridBagLayout());
        
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.gridx = 0;
        c.gridy = 0;
        JButton addBtn = new JButton("Добавить");
        add(addBtn, c);
        JTextField nameTF = new JTextField(15);
        c.gridy++;
        add(nameTF, c);
        List<UserGroup> userGroups = new ArrayList<UserGroup>();
        try {
            userGroups = ServerService.getInstance().getUserGroupFull();
            UserGroup ugDefault = new UserGroup();
            ugDefault.name = "Не выбрано";
            ugDefault.id = -1000;
            userGroups.add(0, ugDefault);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
        final List<UserGroup> userGroupFinal = userGroups;
        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                    JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(self);
                    MenuUserAdd muAdd = new MenuUserAdd(topFrame, "Создание пользователя", true);
                    muAdd.addWindowListener(new WindowAdapter() {
                        public void windowClosing(WindowEvent e) {
                        }
                    });
                    
                    muAdd.setPane(userGroupFinal);
                    muAdd.pack();
                    muAdd.setLocationRelativeTo(null);
                    muAdd.setVisible(true);
            }
        });
        final JComboBox groupCB = new JComboBox(userGroups.toArray());
        c.gridx++;
        add(groupCB, c);
        List<UserShort> userShorts = new ArrayList<UserShort>();
        try {
            userShorts = ServerService.getInstance().getUserShortFull();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
        
        final List<UserShort> finalUserShorts = userShorts;
        final Map<Integer, UserGroup> userGroupById = userGroups.stream().collect(
            Collectors.toMap(item -> item.id, item -> item));
        final JTable userTbl = new JTable(new AbstractTableModel() {
            public int getColumnCount() {
                return 3;
            }
            public int getRowCount() {
                int id = ((UserGroup)(groupCB.getSelectedItem())).id;
                switch (id) {
                    case -1000: return finalUserShorts.size();
                    default: return finalUserShorts.stream().
                                    filter(item -> item.id == id).
                                    collect(Collectors.toList()).size();
                }
            }
            public String getColumnName(int col) {
                switch (col) {
                    case 0: return "ФИО";
                    case 1: return "Должность";
                    case 2: return "Заходил";
                    default: return "-";
                }
            }
            public Object getValueAt(int row, int col) {
                int id = ((UserGroup)(groupCB.getSelectedItem())).id;
                List<UserShort> currentFinalUserShorts;
                switch (id) {
                    case -1000: { currentFinalUserShorts = finalUserShorts.stream().
                                                collect(Collectors.toList());
                                  break;
                    }
                    default: currentFinalUserShorts = finalUserShorts.stream().
                                    filter(item -> item.id == id).
                                    collect(Collectors.toList()); break;
                }
                UserShort us = (UserShort)(currentFinalUserShorts.get(row));
                switch (col) {
                    case 0: return us.name; 
                    case 1: return userGroupById.get(us.user_group_id);
                    case 2: return us.last_login_at;
                    default: return "-";
                }
            }
        });
        userTbl.setPreferredScrollableViewportSize(new Dimension(500, 70));
        userTbl.setFillsViewportHeight(true);
        userTbl.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table =(JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1 && row != -1) {
                    int modelRow = table.convertRowIndexToModel(row);
                    UserShort userShort = finalUserShorts.get(modelRow);
                    User user = null;
                    try {
                        user = ServerService.getInstance().getUserWithDetails(userShort.id);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage());
                        return;
                    }
                    JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(self);
                    MenuUserEdit muEdit = new MenuUserEdit(topFrame, "Редактирование пользователя", true);
                    muEdit.addWindowListener(new WindowAdapter() {
                        public void windowClosing(WindowEvent e) {
                        }
                    });

                    muEdit.setPane(userGroupFinal, user);
                    muEdit.pack();
                    muEdit.setLocationRelativeTo(null);
                    muEdit.setVisible(true);
                }
            }
        });
        groupCB.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                ((AbstractTableModel)(userTbl.getModel())).fireTableDataChanged();
            }
        });
        JScrollPane scrollPane = new JScrollPane(userTbl);
        c.gridx = 0;
        c.gridwidth = 2;
        c.gridy++;
        add(scrollPane, c);
        return this;
    }
}
