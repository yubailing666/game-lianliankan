package ui;

import model.Cell;
import model.ChessGenerator;
import model.GameBoard;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    int width;
    int height;
    String title;
    StatusPanel statusPanel;
    ControlPanel controlPanel;

    public GameFrame(String title, int width, int height) {
        super(title);
        this.setResizable(false);

        // 1. 初始化棋子生成器
        ChessGenerator chessGenerator = new ChessGenerator();
        int coreSize = 4;
        Cell[][] board = chessGenerator.generateChessBoard(coreSize);
        int totalSize = coreSize + 2;

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
        BoardPanel boardPanel = new BoardPanel(new GameBoard(totalSize, totalSize, board), statusPanel, 0, 100, 800, 800);

        // 5. 创建控制面板（把 statusPanel 和 boardPanel 都传进去）
        this.controlPanel = new ControlPanel(statusPanel, boardPanel, 0, 900, 800, 100);

        // 6. 添加面板
        this.add(this.statusPanel);
        this.add(this.controlPanel);
        this.add(boardPanel);

        // 5. 最后设置可见（修复原代码顺序问题）
        this.setVisible(true);
    }
}