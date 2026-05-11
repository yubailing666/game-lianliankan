package ui;

import model.*;
import javax.swing.*;
import java.awt.*;

/**
 * 游戏主页面 — 把原来 GameFrame 里的游戏内容搬到这里
 */
public class GamePanel extends JPanel {

    private StatusPanel statusPanel;
    private BoardPanel boardPanel;
    private ControlPanel controlPanel;
    private boolean isHardMode;

    public GamePanel(boolean isHardMode) {
        this.isHardMode = isHardMode;
        setLayout(null);
        setBackground(new Color(0x6b5b45));

        // 1. 生成棋盘
        ChessGenerator gen = new ChessGenerator();
        Cell[][] board = isHardMode ? gen.generateHardBoard() : gen.generateEasyBoard();
        int totalRow = board.length;
        int totalCol = board[0].length;

        // 2. 三个子面板
        statusPanel = new StatusPanel(0, 0, 800, 100);
        boardPanel = new BoardPanel(new GameBoard(totalRow, totalCol, board), statusPanel, 0, 100, 800, 800);
        controlPanel = new ControlPanel(statusPanel, boardPanel, 0, 900, 800, 100);

        // 3. 重新开始
        controlPanel.setOnRestart(() -> {
            ChessGenerator gen2 = new ChessGenerator();
            Cell[][] newBoard = isHardMode ? gen2.generateHardBoard() : gen2.generateEasyBoard();
            int newRow = newBoard.length;
            int newCol = newBoard[0].length;
            boardPanel.setGameBoard(new GameBoard(newRow, newCol, newBoard));
            statusPanel.resetGame();
            boardPanel.refreshPairInfo();
        });

        add(statusPanel);
        add(boardPanel);
        add(controlPanel);
    }
}
