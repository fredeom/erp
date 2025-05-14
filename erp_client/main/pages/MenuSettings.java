package main.pages;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Dimension;

//import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
//import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.SwingUtilities;

import java.util.List;
import java.util.ArrayList;

import main.ServerService;
import main.MenuItem;
import main.records.UserGroup;

import main.helpers.Menu;

/**
 * MenuSettings page.
 *
 * @author Alexander Smirnov
 * @version 2025-01-09
 */
public class MenuSettings extends JPanel implements IPage {
    @Override
    public JPanel getPane() {
        JPanel self = this;
        setOpaque(false);

        List<MenuItem> menusFull = new ArrayList<MenuItem>();
        List<MenuItem> menus = null;
        MenuItem rootMenuItem = new MenuItem();
        rootMenuItem.name = "Root";
        menusFull.add(rootMenuItem);
        try {
            menus = ServerService.getInstance().getMenuFull();
            menusFull.addAll(menus);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
                
        setLayout(new GridBagLayout());
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        Menu.addNodes(root, menus);
        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        JTree tree = new JTree(treeModel);
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
        
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.gridx = 0;
        c.gridy = 0;
        JButton addBtn = new JButton("Добавить");
        addBtn.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
                if (tree.getSelectionPath() == null) {
                    JOptionPane.showMessageDialog(null, "Выберите элемент меню");
                    return;
                }
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getSelectionPath().getLastPathComponent();
                int parent_id;
                if (node.getUserObject() instanceof String) {
                    parent_id = 0;
                } else {
                   MenuItem p = (MenuItem)(node.getUserObject());
                   parent_id = p.id;
                }

                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(self);
                MenuSettingsAddEdit msAddEdit = new MenuSettingsAddEdit(topFrame, "Добавить элемент меню", true);
                msAddEdit.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        List<MenuItem> menus2;
                        try {
                            menus2 = ServerService.getInstance().getMenuFull();
                        } catch (Exception ex) {
                            return;
                        }
                        DefaultMutableTreeNode root2 = new DefaultMutableTreeNode("Root");
                        Menu.addNodes(root2, menus2);
                        treeModel.setRoot(root2);
                        for (int i = 0; i < tree.getRowCount(); i++) {
                            tree.expandRow(i);
                        }
                    }
                });
                msAddEdit.setAddPane(parent_id);
                msAddEdit.pack();
                msAddEdit.setLocationRelativeTo(null);
                msAddEdit.setVisible(true);
            }
        });
        add(addBtn, c);
        c.gridx++;
        JButton editBtn = new JButton("Редактировать");
        editBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (tree.getSelectionPath() == null) {
                    JOptionPane.showMessageDialog(null, "Выберите элемент меню");
                    return;
                }
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getSelectionPath().getLastPathComponent();
                if (node.getUserObject() instanceof String) {
                    JOptionPane.showMessageDialog(null, "Нельзя редактировать Root элемент");
                    return;
                }
                MenuItem menuItem = (MenuItem)(node.getUserObject());

                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(self);
                MenuSettingsAddEdit msAddEdit = new MenuSettingsAddEdit(topFrame, "Редактировать элемент меню", true);
                msAddEdit.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        List<MenuItem> menus2;
                        try {
                            menus2 = ServerService.getInstance().getMenuFull();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Не могу загрузить полное меню: " + ex.getMessage());
                            return;
                        }
                        DefaultMutableTreeNode root2 = new DefaultMutableTreeNode("Root");
                        Menu.addNodes(root2, menus2);
                        treeModel.setRoot(root2);
                        for (int i = 0; i < tree.getRowCount(); i++) {
                            tree.expandRow(i);
                        }
                    }
                });
                
                List<MenuItem> menusFull2 = new ArrayList<MenuItem>();
                MenuItem rootMenuItem2 = new MenuItem();
                rootMenuItem2.name = "Root";
                menusFull2.add(rootMenuItem2);
                try {
                    menusFull2.addAll(ServerService.getInstance().getMenuFull());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }

                msAddEdit.setEditPane(menuItem, menusFull2);
                msAddEdit.pack();
                msAddEdit.setLocationRelativeTo(null);
                msAddEdit.setVisible(true);
            }
        });
        add(editBtn, c);
        JButton deleteBtn = new JButton("Удалить");
        deleteBtn.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
                if (tree.getSelectionPath() == null) {
                    JOptionPane.showMessageDialog(null, "Выберите элемент меню");
                    return;
                }
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getSelectionPath().getLastPathComponent();
                int delete_id;
                if (node.getUserObject() instanceof String) {
                    delete_id = 0;
                } else {
                   MenuItem p = (MenuItem)(node.getUserObject());
                   delete_id = p.id;
                }

                if (delete_id > 2) {
                    if (JOptionPane.YES_NO_OPTION == JOptionPane.showConfirmDialog(null,
                        "Вы действительно хотите удалить выбранный элемент меню",
                        "Удалить?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE)) {
                        try {
                            ServerService.getInstance().deleteMenu(delete_id);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, ex.getMessage());
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Не могу удалить значимый элемент");
                }
                List<MenuItem> menus2;
                try {
                    menus2 = ServerService.getInstance().getMenuFull();
                } catch (Exception ex) {
                    return;
                }
                DefaultMutableTreeNode root2 = new DefaultMutableTreeNode("Root");
                Menu.addNodes(root2, menus2);
                treeModel.setRoot(root2);
                for (int i = 0; i < tree.getRowCount(); i++) {
                    tree.expandRow(i);
                }
            }
        });
        c.gridx++;
        add(deleteBtn, c);
        c.gridx = 0;
        c.gridwidth = 2;
        c.gridheight = 2;
        c.gridy++;
        tree.setVisibleRowCount(20);
        JScrollPane scroll = new JScrollPane(tree);
        add(scroll, c);
        JButton sortBtn = new JButton("Сортировка");
        sortBtn.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
                if (tree.getSelectionPath() != null) {
                    List<MenuItem> menuItems = new ArrayList<MenuItem>();
                    int sortItemsCount = treeModel.getChildCount(tree.getSelectionPath().getLastPathComponent());
                    if (sortItemsCount < 2) {
                        JOptionPane.showMessageDialog(null, "Недостаточно элементов для сортировки");
                        return;
                    }
                    for (int i = 0; i < sortItemsCount; ++i) {
                        menuItems.add(
                            (MenuItem)(
                                (
                                    (DefaultMutableTreeNode)(
                                        treeModel.getChild(tree.getSelectionPath().getLastPathComponent(), i)
                                    )
                                ).getUserObject()
                            ));
                    }
                    
                    JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(self);
                    MenuSettingsSort msSort = new MenuSettingsSort(topFrame, "Установка порядка элементов меню", true);
                    msSort.addWindowListener(new WindowAdapter() {
                        public void windowClosing(WindowEvent e) {
                            List<MenuItem> menus2;
                            try {
                                menus2 = ServerService.getInstance().getMenuFull();
                            } catch (Exception ex) {
                                return;
                            }
                            DefaultMutableTreeNode root2 = new DefaultMutableTreeNode("Root");
                            Menu.addNodes(root2, menus2);
                            treeModel.setRoot(root2);
                            for (int i = 0; i < tree.getRowCount(); i++) {
                                tree.expandRow(i);
                            }
                        }
                    });
                    
                    msSort.setPane(menuItems);
                    msSort.pack();
                    msSort.setLocationRelativeTo(null);
                    msSort.setVisible(true);
                }
            }
        });
        c.gridheight = 1;
        c.gridwidth = 1;
        c.gridx = 2;
        add(sortBtn, c);
        JButton permissionBtn = new JButton("Права");
        permissionBtn.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
                if (tree.getSelectionPath() == null) {
                    JOptionPane.showMessageDialog(null, "Необходимо выбрать элемент меню");
                    return;
                }
                MenuItem menuItem = (MenuItem)(((DefaultMutableTreeNode)(tree.getSelectionPath().getLastPathComponent())).getUserObject());
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(self);
                MenuSettingsPermission msPermission = new MenuSettingsPermission(topFrame, "Установка прав групп пользователей", true);
                List<MenuPermission> menuPermissions = null;
                List<UserGroup> userGroups = null;
                try {
                    menuPermissions = ServerService.getInstance().getMenuPermissions(menuItem.id);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Не получилось загрузить пермишены для меню: " + ex.getMessage());
                    return;
                }
                try {
                    userGroups = ServerService.getInstance().getUserGroupFull();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Не получилось загрузить группы пользователей для установки прав меню: " + ex.getMessage());
                    return;
                }
                try {
                    msPermission.setPane(menuItem, menuPermissions, userGroups);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Не получилось загрузить панель прав: " + ex.getMessage());
                    return;
                }
                msPermission.pack();
                msPermission.setLocationRelativeTo(null);
                msPermission.setVisible(true);
            }
        });
        c.gridy++;
        add(permissionBtn, c);

        return this;
    }
}
