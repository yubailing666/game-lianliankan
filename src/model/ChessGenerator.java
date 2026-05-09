package model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import utils.Utils;

// 连连看棋子生成器，支持简单/困难两种模式
public class ChessGenerator {
    private static final int BORDER_ICON_INDEX = 0;
    private static final int MAX_RETRY_COUNT = 100;

    // 简单模式：两块互不相邻的4×4独立网格，5种棋子类型
    public Cell[][] generateEasyBoard() {
        int totalRow = 12;
        int totalCol = 12;
        List<Position> validPositions = new ArrayList<>();

        // 第一块4×4网格：从上数第2-5行，从左数第3-6列（1-indexed）
        // 0-indexed: 行1-4，列2-5
        for (int r = 1; r <= 4; r++) {
            for (int c = 2; c <= 5; c++) {
                validPositions.add(new Position(r, c));
            }
        }
        // 第二块4×4网格：从上数第7-10行，从左数第8-11列（1-indexed）
        // 0-indexed: 行6-9，列7-10
        for (int r = 6; r <= 9; r++) {
            for (int c = 7; c <= 10; c++) {
                validPositions.add(new Position(r, c));
            }
        }

        return generateBoard(validPositions, totalRow, totalCol, 5);
    }

    // 困难模式：完整的10×10连续网格，12种棋子类型
    public Cell[][] generateHardBoard() {
        int totalRow = 12;
        int totalCol = 11;
        List<Position> validPositions = new ArrayList<>();

        // 10×10连续网格：从上数第2-11行，从左数第3-12列（1-indexed）
        // 0-indexed: 行1-10，列2-11
        for (int r = 1; r <= 10; r++) {
            for (int c = 1; c <= 9; c++) {
                validPositions.add(new Position(r, c));
            }
        }

        return generateBoard(validPositions, totalRow, totalCol, 12);
    }

    // 通用棋盘生成（带重试机制，确保无死局）
    private Cell[][] generateBoard(List<Position> validPositions, int totalRow, int totalCol, int chessTypeCount) {
        if (validPositions.size() % 2 != 0) {
            throw new IllegalArgumentException("有效位置数量必须为偶数");
        }

        for (int attempt = 0; attempt < MAX_RETRY_COUNT; attempt++) {
            Cell[][] board = new Cell[totalRow][totalCol];

            // 初始化所有格子为空
            for (int i = 0; i < totalRow; i++) {
                for (int j = 0; j < totalCol; j++) {
                    board[i][j] = new Cell(new Position(i, j), true, BORDER_ICON_INDEX);
                }
            }

            // 生成成对的图标列表并打乱
            List<Integer> iconList = generatePairedIconList(validPositions.size(), chessTypeCount);
            Collections.shuffle(iconList);

            // 填充有效位置
            for (int idx = 0; idx < validPositions.size(); idx++) {
                Position pos = validPositions.get(idx);
                board[pos.getRow()][pos.getCol()] = new Cell(pos, false, iconList.get(idx));
            }

            // 验证是否存在至少一条可消除路径
            if (hasValidPair(board)) {
                return board;
            }
        }

        // 超出重试次数，返回最后生成的棋盘
        Cell[][] board = new Cell[totalRow][totalCol];
        for (int i = 0; i < totalRow; i++) {
            for (int j = 0; j < totalCol; j++) {
                board[i][j] = new Cell(new Position(i, j), true, BORDER_ICON_INDEX);
            }
        }
        List<Integer> iconList = generatePairedIconList(validPositions.size(), chessTypeCount);
        Collections.shuffle(iconList);
        for (int idx = 0; idx < validPositions.size(); idx++) {
            Position pos = validPositions.get(idx);
            board[pos.getRow()][pos.getCol()] = new Cell(pos, false, iconList.get(idx));
        }
        return board;
    }

    // 检查棋盘是否存在至少一个可消除的对子
    private boolean hasValidPair(Cell[][] board) {
        int totalRow = board.length;
        int totalCol = board[0].length;
        List<Position> nonEmptyCells = new ArrayList<>();

        for (int i = 0; i < totalRow; i++) {
            for (int j = 0; j < totalCol; j++) {
                if (!board[i][j].isEmpty()) {
                    nonEmptyCells.add(new Position(i, j));
                }
            }
        }

        GameBoard gameBoard = new GameBoard(totalRow, totalCol, board);

        for (int i = 0; i < nonEmptyCells.size(); i++) {
            for (int j = i + 1; j < nonEmptyCells.size(); j++) {
                Position posA = nonEmptyCells.get(i);
                Position posB = nonEmptyCells.get(j);
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

    // 生成成对的图标列表（平均分布到各类型，保证每种出现偶数次）
    private List<Integer> generatePairedIconList(int totalCells, int chessTypeCount) {
        List<Integer> iconList = new ArrayList<>();
        int pairCount = totalCells / 2;
        int basePairs = pairCount / chessTypeCount;
        int extraPairs = pairCount % chessTypeCount;

        for (int type = 0; type < chessTypeCount; type++) {
            int pairsForThisType = basePairs + (type < extraPairs ? 1 : 0);
            for (int p = 0; p < pairsForThisType; p++) {
                iconList.add(type + 1);
                iconList.add(type + 1);
            }
        }
        return iconList;
    }
}
