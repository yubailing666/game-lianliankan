package model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import utils.Utils;

// 连连看棋子生成器，专门用于生成指定规格的成对棋子和棋盘
public class ChessGenerator {
    private static final int DEFAULT_CHESS_TYPE_COUNT = 3;
    private static final int BORDER_ICON_INDEX = 0;
    private static final int MAX_RETRY_COUNT = 100;

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
        
        // 尝试生成有效棋盘，避免死局
        for (int attempt = 0; attempt < MAX_RETRY_COUNT; attempt++) {
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

            // 4. 验证是否存在至少一个可消除的对子
            if (hasValidPair(board, totalSize)) {
                return board;
            }
        }

        // 如果多次重试仍然失败，返回最后一次生成的棋盘
        Cell[][] board = new Cell[totalSize][totalSize];
        for (int i = 0; i < totalSize; i++) {
            for (int j = 0; j < totalSize; j++) {
                board[i][j] = new Cell(new Position(i, j), true, BORDER_ICON_INDEX);
            }
        }
        initBoardBorder(board, totalSize);
        fillCoreChess(board, coreSize, chessTypeCount);
        return board;
    }

    // 检查棋盘是否存在至少一个可消除的对子
    private boolean hasValidPair(Cell[][] board, int totalSize) {
        List<Position> nonEmptyCells = new ArrayList<>();

        // 收集所有非空单元格（只检查核心区域）
        for (int i = 1; i <= totalSize - 2; i++) {
            for (int j = 1; j <= totalSize - 2; j++) {
                if (!board[i][j].isEmpty()) {
                    nonEmptyCells.add(new Position(i, j));
                }
            }
        }

        // 创建临时 GameBoard 用于路径检测
        GameBoard gameBoard = new GameBoard(totalSize, totalSize, board);

        // 检查所有相同图标的对子是否可连接
        for (int i = 0; i < nonEmptyCells.size(); i++) {
            for (int j = i + 1; j < nonEmptyCells.size(); j++) {
                Position posA = nonEmptyCells.get(i);
                Position posB = nonEmptyCells.get(j);

                // 图标相同且可以连接
                if (board[posA.getRow()][posA.getCol()].getIconIndex() ==
                    board[posB.getRow()][posB.getCol()].getIconIndex()) {
                    if (Utils.canLinkAB(gameBoard, posA, posB)) {
                        return true;
                    }
                }
            }
        }

        return false;
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