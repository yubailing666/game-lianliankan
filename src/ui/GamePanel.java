package ui;

import model.*;

import javax.swing.*;
import java.awt.*;

/**
 * 游戏主面板 — 组合所有游戏子面板并连接回调
 *
 * 布局（1000×1000）：
 *   ├── StatusPanel   (0, 0,    800, 100)   — 顶部状态栏
 *   ├── BoardPanel    (0, 100,  800, 800)   — 棋盘区域
 *   ├── ControlPanel  (0, 900,  800, 100)   — 底部控制按钮
 *   └── CatPanel      (800, 0,  200, 1000)  — 右侧猫面板
 *
 * 回调链：
 *   ControlPanel.onRestart  → 重新生成棋盘 + 重置状态
 *   BoardPanel.onFishFeed   → CatPanel.feedFish()
 *   BoardPanel.onWinCallback → 写入 LeaderBoard
 *   ControlPanel.onLeaderBoard → 弹出排行榜窗口
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

        setLayout(null);
        setBackground(new Color(0x6b5b45));

        // ── 生成棋盘 ──
        ChessGenerator gen = new ChessGenerator();
        Cell[][] board = isHardMode ? gen.generateHardBoard() : gen.generateEasyBoard();
        int totalRow = board.length;
        int totalCol = board[0].length;

        // ── 创建子面板（左侧 800px 宽） ──
        statusPanel = new StatusPanel(0, 0, 800, 100);
        boardPanel = new BoardPanel(new GameBoard(totalRow, totalCol, board), statusPanel,
                0, 100, 800, 800);
        controlPanel = new ControlPanel(statusPanel, boardPanel, 0, 900, 800, 100);

        // ── 右侧猫面板（200px 宽） ──
        catPanel = new CatPanel();
        catPanel.setBounds(800, 0, 200, 1000);

        add(statusPanel);
        add(boardPanel);
        add(controlPanel);
        add(catPanel);

        // ── 回调连接 ──

        // 重新开始：重新生成棋盘 + 重置状态
        controlPanel.setOnRestart(() -> {
            ChessGenerator gen2 = new ChessGenerator();
            Cell[][] newBoard = isHardMode ? gen2.generateHardBoard() : gen2.generateEasyBoard();
            int newRow = newBoard.length;
            int newCol = newBoard[0].length;
            boardPanel.setGameBoard(new GameBoard(newRow, newCol, newBoard));
            statusPanel.resetGame();
            boardPanel.refreshPairInfo();
        });

        // 消除棋子 → 喂猫
        boardPanel.setOnFishFeed(() -> catPanel.feedFish());

        // 排行榜按钮
        controlPanel.setOnLeaderBoard(() -> {
            LeaderBoardPanel panel = new LeaderBoardPanel(null, leaderBoard);
            panel.setVisible(true);
        });

        // 胜利回调 → 记录成绩到排行榜
        boardPanel.setOnWinCallback(() -> {
            String mode = isHardMode ? "困难模式" : "简单模式";
            LeaderRecord record = new LeaderRecord(username, mode,
                    statusPanel.getScore(), statusPanel.getTimeUsed());
            leaderBoard.addRecord(record);
        });
    }
}
