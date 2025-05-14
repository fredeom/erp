package main.pages;

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;

/**
 * Settings page.
 *
 * @author Alexander Smirnov
 * @version 2025-01-09
 */
public class Settings extends JPanel implements IPage {
    @Override
    public JPanel getPane() {
        setOpaque(false);
        setBackground(Color.RED);
        add(new JButton("Push me and then just touch me"));
        add(new JLabel("Do I can get my satisfaction"));
        return this;
    }
}
