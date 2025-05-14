package main.pages;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.List;
import java.awt.Dimension;
import javax.swing.DefaultListModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import main.MenuItem;
import main.ServerService;
import main.pages.MenuPermission;
import main.records.UserGroup;

/**
 * In MenuSettingsPermission we set permissions for user groups.
 *
 * @author Alexander Smirnov
 * @version 2025-02-02
 */
public class MenuSettingsPermission extends JDialog
{
    public MenuSettingsPermission(JFrame frame, String title, boolean modal) {
        super(frame, title, modal);
    }
    
    public void setPane(MenuItem menuItem, List<MenuPermission> menuPermissions, List<UserGroup> userGroups) throws Exception {
        JDialog self = this;
        DefaultListModel dlm = new DefaultListModel();
        JList permissionAppliedList = new JList(dlm);
        MenuPermission mpRoot = new MenuPermission();
        mpRoot.name = "menu_" + menuItem.id;
        menuPermissions.add(0, mpRoot);
        JComboBox permissionCB = new JComboBox(menuPermissions.toArray(new MenuPermission[0]));
        JTextField newPermissionTF = new JTextField(15);
        List<String> affectedPermissions = new ArrayList<String>();

        getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.gridx = 0;
        c.gridy = 0;
        JButton minusPermissionBtn = new JButton("−");
        minusPermissionBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (permissionCB.getSelectedIndex() == 0) {
                    JOptionPane.showMessageDialog(null, "Нельзя удалить разрешение для самого элемента меню");
                    return;
                }
                MenuPermission menuPermission = (MenuPermission)permissionCB.getSelectedItem();
                try {
                    ServerService.getInstance().deletePermission(menuPermission.name);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
                permissionCB.removeItemAt(permissionCB.getSelectedIndex());
                permissionCB.setSelectedIndex(permissionCB.getItemCount() - 1);
                try {
                    updatePermissionList(permissionAppliedList, ((MenuPermission)(permissionCB.getSelectedItem())).name);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Не получилось обновить список группы пользователей с выбранным разрешением: " + ex.getMessage());
                }
            }
        });
        getContentPane().add(minusPermissionBtn, c);
        permissionCB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    updatePermissionList(permissionAppliedList, ((MenuPermission)(((JComboBox)e.getSource()).getSelectedItem())).name);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Не получилось обновить список группы пользователей с выбранным разрешением: " + ex.getMessage());
                }
            }
        });
        c.gridx++;
        getContentPane().add(permissionCB, c);
        JButton plusPermissionBtn = new JButton("+");
        plusPermissionBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (newPermissionTF.getText().length() < 3) {
                    JOptionPane.showMessageDialog(null, "Слишком короткое правило, должно быть не менее 3х символов");
                    return;
                }
                MenuPermission menuPermission = null;
                try {
                    menuPermission = ServerService.getInstance().addMenuPermission(newPermissionTF.getText(), menuItem.id);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
                if (menuPermission == null) {
                    JOptionPane.showMessageDialog(null, "Не получилось добавить правило для элемента меню");
                    return;
                }
                permissionCB.addItem(menuPermission);
                permissionCB.setSelectedIndex(permissionCB.getItemCount() - 1);
                DefaultListModel dlm = (DefaultListModel)(permissionAppliedList.getModel());
                dlm.removeAllElements();
            }
        });
        c.gridx++;
        getContentPane().add(plusPermissionBtn, c);
        c.gridx++;
        getContentPane().add(newPermissionTF, c);
        JComboBox groupCB = new JComboBox(userGroups.toArray(new UserGroup[0]));
        c.gridy++;
        c.gridx = 1;
        getContentPane().add(groupCB, c);
        JButton addGroupPermissionBtn = new JButton("+");
        addGroupPermissionBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                UserGroup userGroup = (UserGroup)(groupCB.getSelectedItem());
                for (int i = 0; i < dlm.getSize(); ++i) {
                    if (((UserGroup)(dlm.getElementAt(i))).id == userGroup.id) {
                        JOptionPane.showMessageDialog(null, "Данная группа уже в списке");
                        return;
                    }
                }
                dlm.addElement(userGroup);
                MenuPermission menuPermission = (MenuPermission)(permissionCB.getSelectedItem());
                try {
                    ServerService.getInstance().addUserGroupForPermission(menuPermission.name, userGroup.id);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
                affectedPermissions.add(menuPermission.name + "///" + userGroup.id);
            }
        });
        c.gridx++;
        getContentPane().add(addGroupPermissionBtn, c);
        permissionAppliedList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList)evt.getSource();
                if (evt.getClickCount() == 2) {
                    int ind = list.locationToIndex(evt.getPoint());
                    UserGroup userGroup = (UserGroup)(dlm.get(ind));
                    dlm.remove(ind);
                    MenuPermission menuPermission = (MenuPermission)(permissionCB.getSelectedItem());
                    try {
                        ServerService.getInstance().deleteUserGroupForPermission(menuPermission.name, userGroup.id);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage());
                    }
                    affectedPermissions.add(menuPermission.name + "///" + userGroup.id);
                }
            }
        });
        updatePermissionList(permissionAppliedList, "menu_" + menuItem.id);
        JScrollPane listScroller = new JScrollPane(permissionAppliedList);
        listScroller.setPreferredSize(new Dimension(100, 200));
        c.gridx = 1;
        c.gridy++;
        getContentPane().add(listScroller, c);
        JButton applyChanges = new JButton("Применить");
        applyChanges.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    ServerService.getInstance().applyGroupPermissionByAffected(affectedPermissions);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
                self.dispose();
            }
        });
        c.gridy++;
        getContentPane().add(applyChanges, c);
    }
    
    private void updatePermissionList(JList list, String permissionName) throws Exception {
        List<UserGroup> userGroups;
        try {
            userGroups = ServerService.getInstance().getUserGroupsForPermission(permissionName);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Не могу загрузить группы пользователей для разрешения <" + permissionName + ">: " + ex.getMessage());
            throw ex;
        }
        DefaultListModel dlm = (DefaultListModel)(list.getModel());
        dlm.removeAllElements();
        dlm.addAll(userGroups);
    }
}
