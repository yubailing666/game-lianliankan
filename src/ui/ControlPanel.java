package ui;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {
    StatusPanel statusPanel;
    JButton startButton;
    JButton restartButton;
    JButton settingsButton;
    JButton leaderBoardBtn;
    Runnable onRestart;
    Runnable onLeaderBoard;
    int offSetX;
    int offSetY;
    int width;
    int height;
    int currentTimeLimit = 120;
    int currentCoreSize = 4;

    public void setOnRestart(Runnable callback) {
        this.onRestart = callback;
    }

    public void setOnLeaderBoard(Runnable callback) {
        this.onLeaderBoard = callback;
    }

    public ControlPanel(StatusPanel statusPanel, BoardPanel boardPanel, int offSetX, int offSetY, int width, int height) {
        this.setLayout(null);
        this.setBounds(offSetX, offSetY, width, height);
        setBackground(new Color(0x5c4a3a));
        setOpaque(true);
        this.offSetX = offSetX;
        this.offSetY = offSetY;
        this.width = width;
        this.height = height;
        this.startButton = createStyledButton("> START", new Color(0xe8c87a), new Color(0x4a3d2e));
        this.statusPanel = statusPanel;
        int btnWidth = 130;
        int btnHeight = 40;
        int gap = 12;
        int totalW = btnWidth * 4 + gap * 3;
        int x = (width - totalW) / 2;
        int y = (height - btnHeight) / 2;

        startButton.setBounds(x, y, btnWidth, btnHeight);
        this.add(startButton);
        this.startButton.addActionListener(e -> {
            statusPanel.startGame();
            boardPanel.startGame();
            boardPanel.refreshPairInfo();
        });

        restartButton = createStyledButton("RESTART", new Color(0x6b5b45), new Color(0xc4b091));
        restartButton.setBounds(x + btnWidth + gap, y, btnWidth, btnHeight);
        this.add(restartButton);
        restartButton.addActionListener(e -> {
            if (onRestart != null) onRestart.run();
        });

        settingsButton = createStyledButton("SETTINGS", new Color(0x5c4a3a), new Color(0xa09070));
        settingsButton.setBounds(x + 2 * (btnWidth + gap), y, btnWidth, btnHeight);
        this.add(settingsButton);
        settingsButton.addActionListener(e -> {
            SettingsDialog dlg = new SettingsDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    currentTimeLimit, currentCoreSize);
            dlg.setVisible(true);
            if (dlg.isRestartRequested()) {
                currentTimeLimit = dlg.getSelectedTimeSeconds();
                currentCoreSize = dlg.getSelectedCoreSize();
                if (onRestart != null) onRestart.run();
            }
        });

        leaderBoardBtn = createStyledButton("RANK", new Color(0x4a3d2e), new Color(0xe8c87a));
        leaderBoardBtn.setBounds(x + 3 * (btnWidth + gap), y, btnWidth, btnHeight);
        this.add(leaderBoardBtn);
        leaderBoardBtn.addActionListener(e -> {
            if (onLeaderBoard != null) onLeaderBoard.run();
        });
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
