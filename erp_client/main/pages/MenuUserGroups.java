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

import main.ServerService;
import main.records.UserGroup;
import main.records.UserShort;
import main.records.User;

/**
 * UserGroups' info will be stored in this class.
 *
 * @author Alexander Smirnov
 * @version 2025-04-27
 */
public class MenuUserGroups extends JPanel implements IPage {
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
        List<UserGroup> userGroups = new ArrayList<UserGroup>();
        try {
            userGroups = ServerService.getInstance().getUserGroupFull();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
        final JList<UserGroup> ugList = new JList(userGroups.toArray());
        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String inputValue = JOptionPane.showInputDialog("Please input a value");
                System.out.println(inputValue);
                boolean isExists = false;
                for (int i = 0; i < ugList.getModel().getSize(); ++i) {
                    UserGroup userGroup = ugList.getModel().getElementAt(i);
                    if (userGroup.name.equals(inputValue)) {
                        isExists = true;
                        break;
                    }
                }
                if (isExists) {
                    JOptionPane.showMessageDialog(null, "impossible to create");
                } else {
                    try {
                        ServerService.getInstance().addUserGroup(inputValue);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage());
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(ugList);
        c.gridx = 0;
        c.gridwidth = 2;
        c.gridy++;
        add(scrollPane, c);
        return this;
    }
}
