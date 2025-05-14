import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTabbedPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

/**
 * Class First is here.
 *
 * @author Alexander Smirnov
 * @version 2025-01-03
 */
public class First
{
    public static void main() {
        SwingUtilities.invokeLater( () -> {
            JFrame frame = new JFrame("ERP Client v0.0.1");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            Container contents = frame.getContentPane();
            JTabbedPane top = new JTabbedPane(JTabbedPane.LEFT);
            top.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
            for (int i = 1; i <= 3; i++) {
                top.addTab("Tab " + i, new JLabel("This is Tab " + i));
            }
            top.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent evt) {
                    System.out.println("Change");
                }
            });

            contents.add(top);
            
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
