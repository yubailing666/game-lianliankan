package ui;

import model.*;
import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {

    private BoardPanel boardPanel;
    private StatusPanel statusPanel;
    private ControlPanel controlPanel;
    private CatPanel catPanel;
    private boolean isHardMode;
    private String username;

    public GamePanel(boolean isHardMode, LeaderBoard leaderBoard, String username) {
        this.isHardMode = isHardMode;
        this.username = username;
        setLayout(null);
        setBackground(new Color(0x6b5b45));

        // 生成棋盘
        ChessGenerator gen = new ChessGenerator();
        Cell[][] board = isHardMode ? gen.generateHardBoard() : gen.generateEasyBoard();
        int totalRow = board.length;
        int totalCol = board[0].length;

        // 游戏区 (左 800px)
        statusPanel = new StatusPanel(0, 0, 800, 100);
        boardPanel = new BoardPanel(new GameBoard(totalRow, totalCol, board), statusPanel, 0, 100, 800, 800);
        controlPanel = new ControlPanel(statusPanel, boardPanel, 0, 900, 800, 100);

        // 猫侧栏 (右 200px)
        catPanel = new CatPanel();
        catPanel.setBounds(800, 0, 200, 1000);

        add(statusPanel);
        add(boardPanel);
        add(controlPanel);
        add(catPanel);

        // 回调
        controlPanel.setOnRestart(() -> {
            ChessGenerator gen2 = new ChessGenerator();
            Cell[][] newBoard = isHardMode ? gen2.generateHardBoard() : gen2.generateEasyBoard();
            int newRow = newBoard.length;
            int newCol = newBoard[0].length;
            boardPanel.setGameBoard(new GameBoard(newRow, newCol, newBoard));
            statusPanel.resetGame();
            boardPanel.refreshPairInfo();
        });

        boardPanel.setOnFishFeed(() -> catPanel.feedFish());

        controlPanel.setOnLeaderBoard(() -> {
            LeaderBoardPanel panel = new LeaderBoardPanel(null, leaderBoard);
            panel.setVisible(true);
        });

        boardPanel.setOnWinCallback(() -> {
            String mode = isHardMode ? "困难模式" : "简单模式";
            LeaderRecord record = new LeaderRecord(username, mode, statusPanel.getScore(), statusPanel.getTimeUsed());
            leaderBoard.addRecord(record);
        });
    }
}
