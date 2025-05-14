package main.helpers;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import main.MenuItem;

/**
 * Helper to create manage and populate Menu JTree
 *
 * @author Alexander Smirnov
 * @version 2025-01-19
 */
public class Menu {
    public static void addNodes(DefaultMutableTreeNode node, List<MenuItem> menus) {
        menus.forEach(menu -> {
            try {
                if ((menu.parent_id == 0 && (node.getUserObject() instanceof String
                    && ((String)node.getUserObject()).equals("Root"))) ||
                    ((node.getUserObject() instanceof MenuItem) && menu.parent_id == ((MenuItem)node.getUserObject()).id)) {
                    DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(menu);
                    node.add(newNode);
                    addNodes(newNode, menus);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}
