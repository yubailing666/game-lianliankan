package ui;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {
    StatusPanel statusPanel;
    JButton startButton;
    JButton restartButton;
    JButton settingsButton;
    Runnable onRestart;
    int offSetX;
    int offSetY;
    int width;
    int height;
    int currentTimeLimit = 120;
    int currentCoreSize = 4;

    public void setOnRestart(Runnable callback) {
        this.onRestart = callback;
    }

    public ControlPanel(StatusPanel statusPanel, BoardPanel boardPanel, int offSetX, int offSetY, int width, int height) {
        this.setLayout(null);
        this.setBounds(offSetX, offSetY, width, height);
        this.offSetX = offSetX;
        this.offSetY = offSetY;
        this.width = width;
        this.height = height;
        this.startButton = new JButton("start");
        this.statusPanel = statusPanel;
        int btnWidth = 150;
        int btnHeight = 50;
        int x = (width - btnWidth * 3 - 40) / 2;
        int y = (height - btnHeight) / 2;

        startButton.setBounds(x, y, btnWidth, btnHeight);
        startButton.setFont(new Font("Arial", Font.BOLD, 25));
        startButton.setFocusPainted(false);
        this.add(startButton);
        this.startButton.addActionListener(e -> {
            statusPanel.startGame();
            boardPanel.startGame();
            boardPanel.refreshPairInfo();
        });

        restartButton = new JButton("RESTART");
        restartButton.setBounds(x + btnWidth + 20, y, btnWidth, btnHeight);
        restartButton.setFont(new Font("Arial", Font.BOLD, 25));
        this.add(restartButton);
        restartButton.addActionListener(e -> {
            if (onRestart != null) {
                onRestart.run();
            }
        });

        settingsButton = new JButton("Settings");
        settingsButton.setBounds(x + 2 * (btnWidth + 20), y, btnWidth, btnHeight);
        settingsButton.setFont(new Font("Arial", Font.BOLD, 25));
        this.add(settingsButton);
        settingsButton.addActionListener(e -> {
            SettingsDialog dlg = new SettingsDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    currentTimeLimit,
                    currentCoreSize
            );
            dlg.setVisible(true);

            if (dlg.isRestartRequested()) {
                currentTimeLimit = dlg.getSelectedTimeSeconds();
                currentCoreSize = dlg.getSelectedCoreSize();
                if (onRestart != null) {
                    onRestart.run();
                }
            }
        });
    }
}
