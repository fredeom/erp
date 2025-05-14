package main.pages;

import java.util.List;
import javax.swing.JComboBox;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import main.ServerService;
import main.MenuItem;


/**
 * Фрейм для добавления/редактирования нового элемента меню.
 *
 * @author Alexander Smirnov
 * @version 2025.01.19
 */
public class MenuSettingsAddEdit extends JDialog {
    public MenuSettingsAddEdit(JFrame frame, String title, boolean modal) {
        super(frame, title, modal);
    }
    public void setAddPane(int parent_id) {
        getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.gridx = 0;
        c.gridy = 0;
        getContentPane().add(new JLabel("Название меню: "), c);
        c.gridx = 1;
        JTextField nameTf = new JTextField(15);
        getContentPane().add(nameTf, c);
        c.gridx = 0;
        c.gridy++;
        getContentPane().add(new JLabel("Название класса: "), c);
        c.gridx = 1;
        JTextField paneClassTf = new JTextField(15);
        getContentPane().add(paneClassTf, c);
        c.gridx = 0;
        c.gridy++;
        getContentPane().add(new JLabel("Активность меню: "), c);
        c.gridx = 1;
        JCheckBox activeCheck = new JCheckBox();
        getContentPane().add(activeCheck, c);
        c.gridx = 0;
        c.gridwidth = 2;
        c.gridy++;
        JButton addBtn = new JButton("Добавить");
        addBtn.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
                if (nameTf.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Необходимо добавить уникальное название элемента меню");
                    return;
                }
                try {
                    ServerService.getInstance().addMenu(parent_id, nameTf.getText(), paneClassTf.getText(), activeCheck.isSelected() ? 1 : 0);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        });
        getContentPane().add(addBtn, c);
    }

    public void setEditPane(MenuItem menuItem, List<MenuItem> menuItemsFull) {
        getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.gridx = 0;
        c.gridy = 0;
        getContentPane().add(new JLabel("Корень меню: "), c);
        c.gridx = 1;
        JComboBox rootCB = new JComboBox(menuItemsFull.stream().map(el -> el).toArray(MenuItem[]::new));
        rootCB.setSelectedItem(menuItemsFull.stream().filter(el -> el.id == menuItem.parent_id).toArray(MenuItem[]::new)[0]);
        getContentPane().add(rootCB, c);
        c.gridx = 0;
        c.gridy++;
        getContentPane().add(new JLabel("Название меню: "), c);
        c.gridx = 1;
        JTextField nameTf = new JTextField(15);
        nameTf.setText(menuItem.name);
        getContentPane().add(nameTf, c);
        c.gridx = 0;
        c.gridy++;
        getContentPane().add(new JLabel("Название класса: "), c);
        c.gridx = 1;
        JTextField paneClassTf = new JTextField(15);
        paneClassTf.setText(menuItem.pane_class);
        getContentPane().add(paneClassTf, c);
        c.gridx = 0;
        c.gridy++;
        getContentPane().add(new JLabel("Активность меню: "), c);
        c.gridx = 1;
        JCheckBox activeCheck = new JCheckBox();
        activeCheck.setSelected(menuItem.active == 1);
        getContentPane().add(activeCheck, c);
        c.gridx = 0;
        c.gridwidth = 2;
        c.gridy++;
        JButton editBtn = new JButton("Сохранить");
        editBtn.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
                if (nameTf.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Необходимо добавить уникальное название элемента меню");
                    return;
                }
                try {
                    ServerService.getInstance().editMenu(menuItem.id, (MenuItem)rootCB.getSelectedItem(), nameTf.getText(), paneClassTf.getText(), menuItem.posit, activeCheck.isSelected() ? 1 : 0);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        });
        getContentPane().add(editBtn, c);
    }
}
