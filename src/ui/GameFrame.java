package ui;

import javax.swing.*;
import java.awt.*;

/**
 * 主窗口 — CardLayout 多页面容器
 * 页面：splash → login → game
 */
public class GameFrame extends JFrame {

    private CardLayout cardLayout;

    public GameFrame(String title, int width, int height, boolean isHardMode) {
        super(title);
        setSize(width, height);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        setLayout(cardLayout);

        add(new SplashPanel(this), "splash");
        add(new LoginPanel(this), "login");
        add(new GamePanel(isHardMode), "game");

        showPage("splash");
        setVisible(true);
    }

    /** 页面切换方法 */
    public void showPage(String name) {
        cardLayout.show(getContentPane(), name);
    }
}
