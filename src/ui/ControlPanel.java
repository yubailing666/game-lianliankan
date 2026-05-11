package ui;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {
    StatusPanel statusPanel;
    JButton startButton;
    JButton restartButton;       // ★ 新增
    JButton leaderBoardBtn;
    Runnable onRestart;
    Runnable onLeaderBoard;
    int offSetX;
    int offSetY;
    int width;
    int height;  
    public void setOnRestart(Runnable callback) {   // ★ 新增
        this.onRestart = callback;
    }
    public void setOnLeaderBoard(Runnable callback){
        this.onLeaderBoard = callback;
    }

    public ControlPanel(StatusPanel statusPanel,BoardPanel boardPanel, int offSetX, int offSetY,int width, int height) {
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
        int x = (width - btnWidth) / 2;
        int y = (height - btnHeight) / 2;
        startButton.setBounds(x-200, y, btnWidth, btnHeight);
        startButton.setFont(new Font("Arial", Font.BOLD, 25));
        startButton.setFocusPainted(false);
        this.add(startButton);
        this.startButton.addActionListener(e -> {
            statusPanel.startGame();
            boardPanel.startGame();
            boardPanel.refreshPairInfo();
        });
        restartButton = new JButton("Restart");
        restartButton.setBounds(x , y, btnWidth, btnHeight);
        restartButton.setFont(new Font("Arial", Font.BOLD, 25));
        this.add(restartButton);
        restartButton.addActionListener(e -> {
            if (onRestart != null) {
                onRestart.run();
            }
        });
        leaderBoardBtn = new JButton("LeaderBoard");
        leaderBoardBtn.setBounds(x + 200, y, btnWidth+60, btnHeight);
        leaderBoardBtn.setFont(new Font("Arial", Font.BOLD, 25));
        this.add(leaderBoardBtn);
        leaderBoardBtn.addActionListener(e -> {
            if (onLeaderBoard != null) {
                onLeaderBoard.run();
            }
        });

        }


}
