package ui;

import javax.swing.*;
import java.awt.*;

/**
 * 主窗口 — CardLayout 多页面容器
 * 页面：splash → login → game（game 是登录后才创建的）
 */
public class GameFrame extends JFrame {

    private CardLayout cardLayout;
    private String[] pageNames = {"splash", "login", "game"};
    private boolean gameAdded = false;

    public GameFrame(String title, int width, int height) {
        super(title);
        setSize(width, height);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        setLayout(cardLayout);

        add(new SplashPanel(this), "splash");
        add(new LoginPanel(this), "login");

        showPage("splash");
        setVisible(true);
    }

    /** 登录后调这个，创建游戏页并跳过去 */
    public void startGame(boolean isHardMode) {
        if (!gameAdded) {
            add(new GamePanel(isHardMode), "game");
            gameAdded = true;
        }
        showPage("game");
    }

    public void showPage(String name) {
        cardLayout.show(getContentPane(), name);
    }
}
