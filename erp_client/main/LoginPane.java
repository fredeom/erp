package main;

import java.util.List;
import java.awt.Dimension;
import java.awt.CardLayout;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.text.*;

/**
 * LoginPane is for login user and load menu on main frame
 *
 * @author Alexander Smirnov
 * @version 2025-01-07
 */
public class LoginPane extends JPanel
{
    public LoginPane(JFrame frame, JPanel cards) {
        super();
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.weighty = 0;
        JLabel imgLabel = new JLabel();
        imgLabel.setIcon(new ImageIcon("resources/erp_logo.png"));
        this.add(imgLabel, c);
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 1;
        this.add(new JLabel("Логин: "), c);
        c.gridx = 1;
        JTextField loginField = new JTextField("", 15);
        loginField.setPreferredSize(new Dimension(loginField.getPreferredSize().width - 1, loginField.getPreferredSize().height));
        loginField.setMinimumSize(new Dimension(loginField.getPreferredSize().width - 1, loginField.getPreferredSize().height));
        this.add(loginField, c);
        c.gridx = 0;
        c.gridy = 2;
        this.add(new JLabel("Пароль: "), c);
        c.gridx = 1;
        JPasswordField passField = new JPasswordField("", 15);
        passField.setPreferredSize(new Dimension(passField.getPreferredSize().width - 1, passField.getPreferredSize().height));
        passField.setMinimumSize(new Dimension(passField.getPreferredSize().width - 1, passField.getPreferredSize().height));
        this.add(passField, c);
        c.gridy = 3;
        JTextPane errorTxtPane = new JTextPane();
        errorTxtPane.setEditable(false);
        JButton btn = new JButton("Войти");
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String[] credentials = ServerService.getInstance().login(loginField.getText().trim(), passField.getText().trim());
                    List<MenuItem> menus = ServerService.getInstance().getMenu();
                    cards.add(new MainPanel(frame, cards, menus, credentials), "Main Panel");
                    CardLayout cl = (CardLayout)(cards.getLayout());
                    cl.show(cards, "Main Panel");
                } catch (Exception ex) {
                    StyleContext context = new StyleContext();
                    StyledDocument document = new DefaultStyledDocument(context);
                    Style style = context.getStyle(StyleContext.DEFAULT_STYLE);
                    StyleConstants.setForeground(style, Color.RED);
                    try {
                      document.insertString(document.getLength(), "Ошибка: " + ex.getMessage(), style);
                    } catch (BadLocationException badLocationException) {
                      System.err.println("Oops");
                    }
                    errorTxtPane.setDocument(document);
                }
            }
        });
        this.add(btn, c);
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 4;
        JScrollPane scrollPane = new JScrollPane(errorTxtPane);
        scrollPane.setMinimumSize(new Dimension(500, 100));
        scrollPane.setPreferredSize(new Dimension(500, 100));
        this.add(scrollPane, c);
        
        // TEMPORARY
        loginField.setText("alex");
        passField.setText("secret");
        frame.getRootPane().setDefaultButton(btn);
        btn.requestFocus();
    }
}
