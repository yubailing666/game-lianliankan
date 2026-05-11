package ui;

import model.*;
import javax.swing.*;
import java.awt.*;

/**
 * 游戏主页面 — 棋盘 + 状态栏 + 控制栏 + 排行榜 + 猫侧栏
 */
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
        setBackground(new Color(0x6b5b45));
        setLayout(new BorderLayout());

        // ── 左侧游戏区 ──
        JPanel gameArea = new JPanel(null);
        gameArea.setOpaque(false);

        ChessGenerator gen = new ChessGenerator();
        Cell[][] board = isHardMode ? gen.generateHardBoard() : gen.generateEasyBoard();
        int totalRow = board.length;
        int totalCol = board[0].length;

        statusPanel = new StatusPanel(0, 0, 800, 100);
        boardPanel = new BoardPanel(new GameBoard(totalRow, totalCol, board), statusPanel, 0, 100, 800, 800);
        controlPanel = new ControlPanel(statusPanel, boardPanel, 0, 900, 800, 100);

        gameArea.add(statusPanel);
        gameArea.add(boardPanel);
        gameArea.add(controlPanel);

        // ── 右侧猫栏 ──
        catPanel = new CatPanel();

        add(gameArea, BorderLayout.CENTER);
        add(catPanel, BorderLayout.EAST);

        // ── 回调 ──

        // 重新开始
        controlPanel.setOnRestart(() -> {
            ChessGenerator gen2 = new ChessGenerator();
            Cell[][] newBoard = isHardMode ? gen2.generateHardBoard() : gen2.generateEasyBoard();
            int newRow = newBoard.length;
            int newCol = newBoard[0].length;
            boardPanel.setGameBoard(new GameBoard(newRow, newCol, newBoard));
            statusPanel.resetGame();
            boardPanel.refreshPairInfo();
        });

        // 消除一对 → 喂猫
        boardPanel.setOnFishFeed(() -> catPanel.feedFish());

        // 排行榜按钮
        controlPanel.setOnLeaderBoard(() -> {
            LeaderBoardPanel panel = new LeaderBoardPanel(null, leaderBoard);
            panel.setVisible(true);
        });

        // 胜利时记录排行榜
        boardPanel.setOnWinCallback(() -> {
            String mode = isHardMode ? "困难模式" : "简单模式";
            LeaderRecord record = new LeaderRecord(username, mode, statusPanel.getScore(), statusPanel.getTimeUsed());
            leaderBoard.addRecord(record);
        });
    }
}
