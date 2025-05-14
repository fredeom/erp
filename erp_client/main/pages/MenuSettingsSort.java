package main.pages;

import java.util.List;
import java.util.ArrayList;
import javax.swing.JScrollPane;

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

import main.ServerService;
import main.MenuItem;

/**
 * Selected in MenuSettings MenuItems will be sorted in this JDialog by hand.
 *
 * @author Alexander Smirnov
 * @version 2025-01-19
 */
public class MenuSettingsSort extends JDialog {
    public MenuSettingsSort(JFrame frame, String title, boolean modal) {
        super(frame, title, modal);
    }

    public void setPane(List<MenuItem> menuItems) {
        getContentPane().setLayout(new GridBagLayout());
        
        DefaultListModel<MenuItem> listModel = new DefaultListModel<MenuItem>();
        for (MenuItem menuItem : menuItems) {
            listModel.addElement(menuItem);
        }
        JList<MenuItem> list = new JList<MenuItem>(listModel);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 2;
        list.setVisibleRowCount(10);
        JScrollPane scroll = new JScrollPane(list);
        getContentPane().add(scroll, c);
        c.gridheight = 1;
        c.gridx = 1;
        c.gridy = 0;
        JButton upBtn = new JButton("Вверх");
        upBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = list.getSelectedIndex();
                if (selectedIndex > 0) {
                    DefaultListModel<MenuItem> model = (DefaultListModel<MenuItem>) list.getModel();

                    MenuItem selectedItem = model.getElementAt(selectedIndex);
                    MenuItem aboveItem = model.getElementAt(selectedIndex - 1);
        
                    model.set(selectedIndex, aboveItem);
                    model.set(selectedIndex - 1, selectedItem);

                    list.setSelectedIndex(selectedIndex - 1);
                }
            }
        });
        getContentPane().add(upBtn, c);
        c.gridy++;
        JButton downBtn = new JButton("Вниз");
        downBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = list.getSelectedIndex();
                DefaultListModel<MenuItem> model = (DefaultListModel<MenuItem>) list.getModel();
       
                if (selectedIndex < model.getSize() - 1 && selectedIndex >= 0) {
                    MenuItem selectedItem = model.getElementAt(selectedIndex);
                    MenuItem belowItem = model.getElementAt(selectedIndex + 1);
                    
                    model.set(selectedIndex, belowItem);
                    model.set(selectedIndex + 1, selectedItem);
                    
                    list.setSelectedIndex(selectedIndex + 1);
                }
            }
        });
        getContentPane().add(downBtn, c);
        c.gridx = 0;
        c.gridy++;
        JButton saveBtn = new JButton("Сохранить");
        saveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DefaultListModel<MenuItem> model = (DefaultListModel<MenuItem>) list.getModel();
                List<MenuItem> menuItems = new ArrayList<MenuItem>();
                for (int i = 0; i < model.getSize(); ++i) {
                    menuItems.add(model.get(i));
                }
                try {
                    ServerService.getInstance().updateMenuItems(menuItems);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        });
        getContentPane().add(saveBtn, c);
    }
}
