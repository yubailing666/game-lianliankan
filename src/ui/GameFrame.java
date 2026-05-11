package ui;

import model.Cell;
import model.ChessGenerator;
import model.GameBoard;

import javax.swing.*;

public class GameFrame extends JFrame {
    int width;
    int height;
    String title;
    StatusPanel statusPanel;
    ControlPanel controlPanel;
    boolean isHardMode;

    public GameFrame(String title, int width, int height, boolean isHardMode) {
        super(title);
        this.setResizable(false);
        this.isHardMode = isHardMode;

        // 1. 根据难度生成棋盘
        ChessGenerator chessGenerator = new ChessGenerator();
        Cell[][] board = isHardMode ? chessGenerator.generateHardBoard() : chessGenerator.generateEasyBoard();
        int totalRow = board.length;
        int totalCol = board[0].length;

        // 2. 窗口基础设置
        this.title = title;
        this.width = width;
        this.height = height;
        this.setLayout(null);
        this.setSize(width, height);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 3. 先创建 StatusPanel
        this.statusPanel = new StatusPanel(0, 0, 800, 100);

        // 4. 初始化棋盘面板（把 statusPanel 传进去）
        BoardPanel boardPanel = new BoardPanel(new GameBoard(totalRow, totalCol, board), statusPanel, 0, 100, 800, 800);

        // 5. 创建控制面板
        this.controlPanel = new ControlPanel(statusPanel, boardPanel, 0, 900, 800, 100);

        // ★ 重新开始逻辑（保持当前难度）
        controlPanel.setOnRestart(() -> {
            ChessGenerator gen = new ChessGenerator();
            Cell[][] newBoard = isHardMode ? gen.generateHardBoard() : gen.generateEasyBoard();
            int newRow = newBoard.length;
            int newCol = newBoard[0].length;
            boardPanel.setGameBoard(new GameBoard(newRow, newCol, newBoard));
            statusPanel.resetGame();
            boardPanel.refreshPairInfo();
        });

        // 6. 添加面板
        this.add(this.statusPanel);
        this.add(this.controlPanel);
        this.add(boardPanel);

        // 7. 最后设置可见
        this.setVisible(true);
    }
}
