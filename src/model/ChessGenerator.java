package model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// 连连看棋子生成器，专门用于生成指定规格的成对棋子和棋盘
public class ChessGenerator {
    private static final int DEFAULT_CHESS_TYPE_COUNT = 3;
    private static final int BORDER_ICON_INDEX = 0;

    // 生成棋盘（带默认棋子类型数）
    public Cell[][] generateChessBoard(int coreSize) {
        return generateChessBoard(coreSize, DEFAULT_CHESS_TYPE_COUNT);
    }

    // 生成棋盘（核心逻辑）
    public Cell[][] generateChessBoard(int coreSize, int chessTypeCount) {
        // 校验核心尺寸合法性
        if (coreSize <= 0) {
            throw new IllegalArgumentException("核心棋盘尺寸必须大于0");
        }
        if (chessTypeCount <= 0) {
            throw new IllegalArgumentException("棋子类型数必须大于0");
        }

        int totalSize = coreSize + 2; // 上下左右各加1行/列边框
        Cell[][] board = new Cell[totalSize][totalSize];

        // 1. 初始化整个棋盘（先填充所有位置为默认值，避免空指针）
        for (int i = 0; i < totalSize; i++) {
            for (int j = 0; j < totalSize; j++) {
                board[i][j] = new Cell(new Position(i, j), true, BORDER_ICON_INDEX);
            }
        }

        // 2. 初始化边框（复用原有逻辑，明确标记边框）
        initBoardBorder(board, totalSize);

        // 3. 填充核心区域棋子（关键修复：从1,1开始填充，避开边框）
        fillCoreChess(board, coreSize, chessTypeCount);

        return board;
    }

    // 初始化棋盘边框（边框标记为isEmpty=true，图标索引0）
    private void initBoardBorder(Cell[][] board, int totalSize) {
        for (int i = 0; i < totalSize; i++) {
            for (int j = 0; j < totalSize; j++) {
                if (i == 0 || i == totalSize - 1 || j == 0 || j == totalSize - 1) {
                    board[i][j] = new Cell(new Position(i, j), true, BORDER_ICON_INDEX);
                }
            }
        }
    }

    // 填充核心区域棋子（核心修复：坐标偏移）
    private void fillCoreChess(Cell[][] board, int coreSize, int chessTypeCount) {
        int coreTotalCells = coreSize * coreSize;
        if (coreTotalCells % 2 != 0) {
            throw new IllegalArgumentException("核心棋子数量必须为偶数（核心尺寸需为偶数）");
        }

        // 生成成对的棋子图标列表并打乱
        List<Integer> chessIconList = generatePairedIconList(coreTotalCells, chessTypeCount);
        Collections.shuffle(chessIconList);

        int iconIndex = 0;
        // 核心区域：行/列从1开始，到coreSize结束（避开边框）
        for (int i = 1; i <= coreSize; i++) {
            for (int j = 1; j <= coreSize; j++) {
                int icon = chessIconList.get(iconIndex++);
                // 核心棋子标记为非空，设置对应图标索引
                board[i][j] = new Cell(new Position(i, j), false, icon);
            }
        }
    }

    // 生成成对的图标列表（保证每个图标出现两次）
    private List<Integer> generatePairedIconList(int coreTotalCells, int chessTypeCount) {
        List<Integer> iconList = new ArrayList<>();
        int pairCount = coreTotalCells / 2; // 成对数量

        for (int i = 0; i < pairCount; i++) {
            // 循环使用棋子类型（1~chessTypeCount）
            int icon = (i % chessTypeCount) + 1;
            iconList.add(icon);
            iconList.add(icon);
        }
        return iconList;
    }
}