package main.pages;

import java.util.List;
import java.util.ArrayList;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import main.ServerService;
import main.records.UserGroup;

/**
 * In this dialog user is created.
 *
 * @author Alexander Smirnov
 * @version 2025-03-16
 */
public class MenuUserAdd extends JDialog {
    public MenuUserAdd(JFrame frame, String title, boolean modal) {
        super(frame, title, modal);
    }

    public void setPane(List<UserGroup> userGroups) {
        getContentPane().setLayout(new GridBagLayout());
        
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.gridx = 0;
        c.gridy = 0;
        JLabel loginLbl = new JLabel("Логин (vasya001):");
        getContentPane().add(loginLbl, c);
        JTextField loginTF = new JTextField(15);
        c.gridx = 1;
        getContentPane().add(loginTF, c);
        JLabel passLbl = new JLabel("Пароль (paS$@vv0rD#):");
        c.gridx = 0;
        c.gridy++;
        getContentPane().add(passLbl, c);
        JPasswordField passPF = new JPasswordField(15);
        c.gridx = 1;
        getContentPane().add(passPF, c);
        JLabel nameLbl = new JLabel("Имя Фамилия (Василий Пупкин):");
        c.gridx = 0;
        c.gridy++;
        getContentPane().add(nameLbl, c);
        JTextField nameTF = new JTextField(15);
        c.gridx = 1;
        getContentPane().add(nameTF, c);
        JLabel phoneLbl = new JLabel("Телефон (79123123123):");
        c.gridx = 0;
        c.gridy++;
        getContentPane().add(phoneLbl, c);
        JTextField phoneTF = new JTextField(15);
        c.gridx = 1;
        getContentPane().add(phoneTF, c);
        JComboBox userGroupCB = new JComboBox(userGroups.toArray());
        c.gridx = 0;
        c.gridwidth = 2;
        c.gridy++;
        getContentPane().add(userGroupCB, c);
        JButton saveBtn = new JButton("Сохранить");
        saveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String lgn = loginTF.getText();
                String pass= String.valueOf(passPF.getPassword());
                String name= nameTF.getText().trim();
                String phone= phoneTF.getText();
                int userGroupId = ((UserGroup)(userGroupCB.getSelectedItem())).id;
                if (
                    !lgn.contains(" ") && lgn.length() >= 3 &&
                    !pass.contains(" ") && pass.length() >= 6 &&
                    name.length() >= 4 && !phone.contains(" ") &&
                    phone.length() >= 10 && phone.length() <= 13 &&
                    phone.matches("\\d+") && userGroupId > 0
                ) {
                    try {
                        ServerService.getInstance().addUser(lgn, pass, name, phone, userGroupId);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Какое-то из полей не корректно, посмотрите пример.");
                }
            }
        });
        c.gridy++;
        getContentPane().add(saveBtn, c);
    }
}
