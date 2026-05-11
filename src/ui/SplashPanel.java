package ui;

import javax.swing.*;
import java.awt.*;

public class SplashPanel extends JPanel {

    private GameFrame parent;

    public SplashPanel(GameFrame parent) {

        this.parent = parent;
        setLayout(new GridBagLayout());
        setBackground(new Color(0x6b5b45));

        JLabel cue =new JLabel("Click to start");




        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                parent.showPage("login");
            }
        });
    }
}
